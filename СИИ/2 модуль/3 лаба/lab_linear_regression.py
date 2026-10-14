from pathlib import Path
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

DATA_URL = "https://raw.githubusercontent.com/ageron/handson-ml2/master/datasets/housing/housing.csv"
LOCAL_FILE = Path("housing.csv")
RANDOM_SEED = 42
TEST_SHARE = 0.20
TARGET = "median_house_value"

NUMERIC_FEATURES = [
    "longitude",
    "latitude",
    "housing_median_age",
    "total_rooms",
    "total_bedrooms",
    "population",
    "households",
    "median_income",
]


def load_data():
    if LOCAL_FILE.exists():
        return pd.read_csv(LOCAL_FILE)
    return pd.read_csv(DATA_URL)


def lcg_permutation(n, seed=42):
    """Детерминированное перемешивание без sklearn."""
    indices = np.arange(n)
    state = int(seed) & 0xFFFFFFFF
    for i in range(n - 1, 0, -1):
        state = (1664525 * state + 1013904223) & 0xFFFFFFFF
        u = state / 2**32
        j = int(u * (i + 1))
        indices[i], indices[j] = indices[j], indices[i]
    return indices


def train_test_split_manual(df, test_share=0.2, seed=42):
    indices = lcg_permutation(len(df), seed)
    train_size = int(len(df) * (1 - test_share))
    train_idx = indices[:train_size]
    test_idx = indices[train_size:]
    return df.iloc[train_idx].copy(), df.iloc[test_idx].copy()


def prepare_data(train_df, test_df):
    # 1. Заполнение пропусков медианой, рассчитанной только на train.
    bedrooms_median = train_df["total_bedrooms"].median()
    train_df["total_bedrooms"] = train_df["total_bedrooms"].fillna(bedrooms_median)
    test_df["total_bedrooms"] = test_df["total_bedrooms"].fillna(bedrooms_median)

    # Бонусный синтетический признак.
    train_df["bedrooms_per_room"] = train_df["total_bedrooms"] / train_df["total_rooms"]
    test_df["bedrooms_per_room"] = test_df["total_bedrooms"] / test_df["total_rooms"]

    numeric_with_synthetic = NUMERIC_FEATURES + ["bedrooms_per_room"]

    # 2. Z-нормировка. Параметры считаются только по train.
    means = train_df[numeric_with_synthetic].mean()
    stds = train_df[numeric_with_synthetic].std(ddof=0)

    train_z = (train_df[numeric_with_synthetic] - means) / stds
    test_z = (test_df[numeric_with_synthetic] - means) / stds
    train_z.columns = ["z_" + c for c in train_z.columns]
    test_z.columns = ["z_" + c for c in test_z.columns]

    train_proc = pd.concat([train_df.reset_index(drop=True), train_z.reset_index(drop=True)], axis=1)
    test_proc = pd.concat([test_df.reset_index(drop=True), test_z.reset_index(drop=True)], axis=1)

    # 3. One-hot кодирование категориального признака вручную.
    categories = sorted(train_df["ocean_proximity"].dropna().unique())
    baseline = categories[0]  # базовая категория, чтобы избежать линейной зависимости
    for category in categories[1:]:
        col = "ocean_" + category
        train_proc[col] = (train_proc["ocean_proximity"] == category).astype(float)
        test_proc[col] = (test_proc["ocean_proximity"] == category).astype(float)

    return train_proc, test_proc, bedrooms_median, categories, baseline


def gaussian_solve(A, b):
    """Решение A*x=b методом Гаусса с выбором главного элемента.

    np.linalg.inv / lstsq / pinv не используются.
    """
    A = np.array(A, dtype=float, copy=True)
    b = np.array(b, dtype=float, copy=True)
    n = len(b)

    for k in range(n):
        pivot = k + int(np.argmax(np.abs(A[k:, k])))
        if abs(A[pivot, k]) < 1e-12:
            raise ValueError("Матрица нормальных уравнений вырождена")

        if pivot != k:
            A[[k, pivot]] = A[[pivot, k]]
            b[[k, pivot]] = b[[pivot, k]]

        pivot_value = A[k, k]
        A[k, k:] = A[k, k:] / pivot_value
        b[k] = b[k] / pivot_value

        for i in range(n):
            if i == k:
                continue
            factor = A[i, k]
            A[i, k:] = A[i, k:] - factor * A[k, k:]
            b[i] = b[i] - factor * b[k]

    return b


def fit_ols_manual(X, y):
    """МНК через нормальные уравнения: (X^T X) beta = X^T y."""
    X = np.asarray(X, dtype=float)
    y = np.asarray(y, dtype=float)
    X_aug = np.column_stack([np.ones(len(X)), X])

    A = X_aug.T @ X_aug
    b = X_aug.T @ y
    beta = gaussian_solve(A, b)
    return beta


def predict(X, beta):
    X = np.asarray(X, dtype=float)
    X_aug = np.column_stack([np.ones(len(X)), X])
    return X_aug @ beta


def r2_score_manual(y_true, y_pred):
    y_true = np.asarray(y_true, dtype=float)
    y_pred = np.asarray(y_pred, dtype=float)
    ss_res = np.sum((y_true - y_pred) ** 2)
    ss_tot = np.sum((y_true - y_true.mean()) ** 2)
    return 1.0 - ss_res / ss_tot


def evaluate_model(name, train_df, test_df, feature_columns):
    X_train = train_df[feature_columns].to_numpy(dtype=float)
    y_train = train_df[TARGET].to_numpy(dtype=float)
    X_test = test_df[feature_columns].to_numpy(dtype=float)
    y_test = test_df[TARGET].to_numpy(dtype=float)

    beta = fit_ols_manual(X_train, y_train)
    pred_train = predict(X_train, beta)
    pred_test = predict(X_test, beta)

    result = {
        "model": name,
        "features": feature_columns,
        "beta": beta,
        "r2_train": r2_score_manual(y_train, pred_train),
        "r2_test": r2_score_manual(y_test, pred_test),
    }
    return result


def make_plots(df, model_results):
    out = Path("plots")
    out.mkdir(exist_ok=True)

    # Гистограмма целевой переменной.
    plt.figure(figsize=(8, 5))
    plt.hist(df[TARGET], bins=30)
    plt.xlabel("Медианная стоимость жилья")
    plt.ylabel("Количество объектов")
    plt.title("Распределение median_house_value")
    plt.tight_layout()
    plt.savefig(out / "target_hist.png", dpi=160)
    plt.close()

    # Гистограмма дохода.
    plt.figure(figsize=(8, 5))
    plt.hist(df["median_income"], bins=30)
    plt.xlabel("Медианный доход")
    plt.ylabel("Количество объектов")
    plt.title("Распределение median_income")
    plt.tight_layout()
    plt.savefig(out / "income_hist.png", dpi=160)
    plt.close()

    # Частоты категорий.
    counts = df["ocean_proximity"].value_counts()
    plt.figure(figsize=(8, 5))
    counts.plot(kind="bar")
    plt.xlabel("Категория")
    plt.ylabel("Количество объектов")
    plt.title("Распределение ocean_proximity")
    plt.xticks(rotation=25, ha="right")
    plt.tight_layout()
    plt.savefig(out / "ocean_categories.png", dpi=160)
    plt.close()

    # Корреляционная матрица числовых признаков.
    corr = df.select_dtypes(include=[np.number]).corr()
    plt.figure(figsize=(9, 7))
    img = plt.imshow(corr.to_numpy(), vmin=-1, vmax=1)
    plt.colorbar(img)
    plt.xticks(range(len(corr.columns)), corr.columns, rotation=75, ha="right", fontsize=8)
    plt.yticks(range(len(corr.columns)), corr.columns, fontsize=8)
    plt.title("Корреляционная матрица")
    plt.tight_layout()
    plt.savefig(out / "correlation.png", dpi=180)
    plt.close()

    # Сравнение R^2.
    labels = [r["model"] for r in model_results]
    values = [r["r2_test"] for r in model_results]
    plt.figure(figsize=(8, 5))
    bars = plt.bar(labels, values)
    plt.ylim(0, 0.72)
    plt.ylabel("R² на тестовой выборке")
    plt.title("Сравнение моделей")
    for bar, value in zip(bars, values):
        plt.text(bar.get_x() + bar.get_width() / 2, value + 0.01, f"{value:.4f}", ha="center")
    plt.tight_layout()
    plt.savefig(out / "r2_comparison.png", dpi=160)
    plt.close()


def main():
    df = load_data()

    print("Размер датасета:", df.shape)
    print("\nПропуски:\n", df.isna().sum())
    print("\nСтатистика:\n", df.describe(percentiles=[0.25, 0.5, 0.75]).T)
    print("\nКатегории ocean_proximity:\n", df["ocean_proximity"].value_counts())

    train_df, test_df = train_test_split_manual(df, TEST_SHARE, RANDOM_SEED)
    train_proc, test_proc, bedrooms_median, categories, baseline = prepare_data(train_df, test_df)

    dummy_columns = ["ocean_" + c for c in categories[1:]]

    model1_features = ["z_median_income"]
    model2_features = [
        "z_median_income",
        "z_housing_median_age",
        "z_latitude",
        "z_longitude",
    ]
    model3_features = ["z_" + c for c in NUMERIC_FEATURES] + dummy_columns
    bonus_features = model3_features + ["z_bedrooms_per_room"]

    results = [
        evaluate_model("Модель 1", train_proc, test_proc, model1_features),
        evaluate_model("Модель 2", train_proc, test_proc, model2_features),
        evaluate_model("Модель 3", train_proc, test_proc, model3_features),
        evaluate_model("Бонус", train_proc, test_proc, bonus_features),
    ]

    print(f"\nМедиана для заполнения total_bedrooms: {bedrooms_median}")
    print(f"Базовая категория ocean_proximity: {baseline}")
    print(f"Размер train/test: {len(train_proc)}/{len(test_proc)}")

    for result in results:
        print("\n" + result["model"])
        print("Признаки:", result["features"])
        print(f"R² train = {result['r2_train']:.6f}")
        print(f"R² test  = {result['r2_test']:.6f}")
        print("Коэффициенты:", result["beta"])

    make_plots(df, results)


if __name__ == "__main__":
    main()
