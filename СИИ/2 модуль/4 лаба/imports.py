# ============================================================
# Лабораторная работа 4. Метод k-ближайших соседей (датасет Wine)
# ============================================================

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.datasets import load_wine
from sklearn.model_selection import train_test_split
import warnings

warnings.filterwarnings('ignore')


# ============================================================
# 1. ЗАГРУЗКА ДАННЫХ
# ============================================================
data = load_wine()
df = pd.DataFrame(data.data, columns=data.feature_names)
df['target'] = data.target

print("Размер датасета:", df.shape)
print(df.head())


# ============================================================
# 2. ПРЕДВАРИТЕЛЬНАЯ ОБРАБОТКА
# ============================================================
# --- 2.1. Пропуски ---
print("\nПропуски по столбцам:\n", df.isnull().sum())
for col in df.columns:
    if df[col].isnull().any():
        df[col] = df[col].fillna(df[col].median())

# --- 2.2. Кодирование категориальных признаков ---
cat_cols = df.select_dtypes(include=['object']).columns.tolist()
if cat_cols:
    df = pd.get_dummies(df, columns=cat_cols, drop_first=True)
print("Категориальные признаки:", cat_cols if cat_cols else "нет")

# --- 2.3. Разделение и масштабирование ---
X = df.drop('target', axis=1).copy()
y = df['target'].values

X_train, X_test, y_train, y_test = train_test_split(
    X.values, y, test_size=0.3, random_state=42, stratify=y
)

mu = X_train.mean(axis=0)
sigma = X_train.std(axis=0)
sigma[sigma == 0] = 1.0

X_train_scaled = (X_train - mu) / sigma
X_test_scaled  = (X_test  - mu) / sigma

print("Train:", X_train_scaled.shape, "| Test:", X_test_scaled.shape)


# ============================================================
# 3. СТАТИСТИКА И ВИЗУАЛИЗАЦИЯ
# ============================================================
# --- 3.1. Описательная статистика (по масштабированным train) ---
stats = pd.DataFrame({
    'count': X_train_scaled.shape[0],
    'mean':  X_train_scaled.mean(axis=0),
    'std':   X_train_scaled.std(axis=0),
    'min':   X_train_scaled.min(axis=0),
    '25%':   np.percentile(X_train_scaled, 25, axis=0),
    '50%':   np.percentile(X_train_scaled, 50, axis=0),
    '75%':   np.percentile(X_train_scaled, 75, axis=0),
    'max':   X_train_scaled.max(axis=0),
}, index=X.columns)
print("\nОписательная статистика (scaled):\n", stats.round(3))

# Гистограммы масштабированных признаков
pd.DataFrame(X_train_scaled, columns=X.columns).hist(
    figsize=(14, 10), bins=20, edgecolor='black'
)
plt.suptitle("Распределение масштабированных признаков", y=1.02)
plt.tight_layout()
plt.show()

# --- 3.2. 3D-визуализация ---
fig = plt.figure(figsize=(9, 7))
ax = fig.add_subplot(111, projection='3d')

feat_idx = [0, 6, 9]
colors = ['red', 'green', 'blue']
for cls in np.unique(y_train):
    m = y_train == cls
    ax.scatter(X_train_scaled[m, feat_idx[0]],
               X_train_scaled[m, feat_idx[1]],
               X_train_scaled[m, feat_idx[2]],
               c=colors[cls % len(colors)],
               label=f'class {cls}', alpha=0.7, edgecolor='k')

ax.set_xlabel(X.columns[feat_idx[0]])
ax.set_ylabel(X.columns[feat_idx[1]])
ax.set_zlabel(X.columns[feat_idx[2]])
ax.set_title("3D-визуализация признаков (scaled)")
ax.legend()
plt.show()


# ============================================================
# 4. РЕАЛИЗАЦИЯ k-NN С НУЛЯ (только NumPy / Pandas)
# ============================================================
class KNNClassifier:
    """k ближайших соседей. Векторное вычисление расстояний."""

    def __init__(self, k=3):
        self.k = k

    def fit(self, X, y):
        self.X_train = np.asarray(X, dtype=float)
        self.y_train = np.asarray(y)
        return self

    def predict(self, X):
        X = np.asarray(X, dtype=float)

        # ||a - b||^2 = ||a||^2 + ||b||^2 - 2 a·b
        X2 = np.sum(X ** 2, axis=1, keepdims=True)               # (n_test, 1)
        T2 = np.sum(self.X_train ** 2, axis=1, keepdims=True).T  # (1, n_train)
        dists = np.sqrt(np.maximum(X2 + T2 - 2.0 * X @ self.X_train.T, 0.0))

        # индексы k ближайших
        idx = np.argsort(dists, axis=1)[:, :self.k]

        # мажоритарное голосование
        preds = np.empty(X.shape[0], dtype=self.y_train.dtype)
        for i in range(X.shape[0]):
            labels, counts = np.unique(self.y_train[idx[i]], return_counts=True)
            preds[i] = labels[np.argmax(counts)]
        return preds


def confusion_matrix_manual(y_true, y_pred, n_classes):
    cm = np.zeros((n_classes, n_classes), dtype=int)
    for t, p in zip(y_true, y_pred):
        cm[t, p] += 1
    return cm


def accuracy(y_true, y_pred):
    return np.mean(y_true == y_pred)


# ============================================================
# 5. ДВЕ МОДЕЛИ k-NN С РАЗНЫМИ НАБОРАМИ ПРИЗНАКОВ
# ============================================================
# --- Модель 1: случайные признаки ---
np.random.seed(42)
n_features = X_train_scaled.shape[1]
random_idx = np.random.choice(
    n_features, size=max(3, n_features // 2), replace=False
)
print("\nМодель 1 — случайные признаки:", list(X.columns[random_idx]))

# --- Модель 2: фиксированный набор ---
fixed_names = ['alcohol', 'flavanoids', 'color_intensity',
               'proline', 'total_phenols']
fixed_names = [f for f in fixed_names if f in X.columns]
fixed_idx = [X.columns.get_loc(f) for f in fixed_names]
print("Модель 2 — фиксированные признаки:", fixed_names)

X1_train, X1_test = X_train_scaled[:, random_idx], X_test_scaled[:, random_idx]
X2_train, X2_test = X_train_scaled[:, fixed_idx],  X_test_scaled[:, fixed_idx]


# ============================================================
# 6. ОЦЕНКА ПРИ РАЗНЫХ k И МАТРИЦЫ ОШИБОК
# ============================================================
k_values = [1, 3, 5, 7, 10, 15]
n_classes = len(np.unique(y))

results = {}

for name, (Xtr, Xte) in {
    'Model 1 (random features)': (X1_train, X1_test),
    'Model 2 (fixed features)':  (X2_train, X2_test),
}.items():
    print("=" * 60)
    print(name)
    results[name] = {}
    for k in k_values:
        knn = KNNClassifier(k=k).fit(Xtr, y_train)
        y_pred = knn.predict(Xte)
        acc = accuracy(y_test, y_pred)
        cm = confusion_matrix_manual(y_test, y_pred, n_classes)

        results[name][k] = {'acc': acc, 'cm': cm}
        print(f"  k={k:>2}: accuracy = {acc:.4f}")
        print(pd.DataFrame(
            cm,
            index=[f"true {i}"  for i in range(n_classes)],
            columns=[f"pred {i}" for i in range(n_classes)]
        ))

# --- 6.1. Визуализация матриц ошибок ---
fig, axes = plt.subplots(2, len(k_values), figsize=(3 * len(k_values), 6))
for row, name in enumerate(results.keys()):
    for col, k in enumerate(k_values):
        cm = results[name][k]['cm']
        ax = axes[row, col]
        ax.imshow(cm, cmap='Blues')
        for i in range(n_classes):
            for j in range(n_classes):
                ax.text(j, i, cm[i, j], ha='center', va='center',
                        color='black', fontsize=9)
        short = "M1" if "1" in name else "M2"
        ax.set_title(f"{short} | k={k}\nacc={results[name][k]['acc']:.2f}", fontsize=9)
        ax.set_xticks(range(n_classes))
        ax.set_yticks(range(n_classes))
        ax.set_xlabel("pred")
        ax.set_ylabel("true")
plt.tight_layout()
plt.show()

# --- 6.2. График зависимости точности от k ---
plt.figure(figsize=(8, 5))
for name, d in results.items():
    plt.plot(k_values, [d[k]['acc'] for k in k_values],
             marker='o', label=name)
plt.xlabel("k")
plt.ylabel("accuracy")
plt.title("Зависимость точности от k")
plt.grid(True)
plt.legend()
plt.show()