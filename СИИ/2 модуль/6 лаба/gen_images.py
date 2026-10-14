"""
Генерация графиков для отчёта Лабораторной работы №6
"""
import numpy as np
import pandas as pd
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import seaborn as sns
import warnings
warnings.filterwarnings('ignore')

df = sns.load_dataset('titanic')
features = df[['pclass', 'sex', 'age', 'sibsp', 'parch', 'fare', 'embarked']].copy()
target = df['survived'].copy()
features['sex'] = (features['sex'] == 'female').astype(int)
features = pd.get_dummies(features, columns=['embarked'], drop_first=True, dtype=int)
features = pd.get_dummies(features, columns=['pclass'], drop_first=True, dtype=int, prefix='pclass')
features['age'] = features['age'].fillna(features['age'].median())
features['fare'] = features['fare'].fillna(features['fare'].median())

from sklearn.preprocessing import StandardScaler
from sklearn.model_selection import train_test_split
scaler = StandardScaler()
X_scaled = pd.DataFrame(scaler.fit_transform(features), columns=features.columns)
X = X_scaled.values
y = target.values.reshape(-1, 1)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)


class LogisticRegressionGD:
    def __init__(self, learning_rate=0.01, n_iterations=1000):
        self.lr = learning_rate; self.n_iter = n_iterations; self.loss_history = []

    @staticmethod
    def _sigmoid(z):
        return 1 / (1 + np.exp(-np.clip(z, -500, 500)))

    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.weights = np.zeros((n_features, 1)); self.bias = 0.0; self.loss_history = []
        for _ in range(self.n_iter):
            y_pred = self._sigmoid(np.dot(X, self.weights) + self.bias)
            dw = (1/n_samples) * np.dot(X.T, (y_pred - y))
            db = (1/n_samples) * np.sum(y_pred - y)
            self.weights -= self.lr * dw; self.bias -= self.lr * db
            self.loss_history.append(-np.mean(y * np.log(np.clip(y_pred, 1e-15, 1-1e-15)) + (1-y) * np.log(np.clip(1-y_pred, 1e-15, 1-1e-15))))
        return self

    def predict(self, X):
        return (self._sigmoid(np.dot(X, self.weights) + self.bias) >= 0.5).astype(int)

    def predict_proba(self, X):
        return self._sigmoid(np.dot(X, self.weights) + self.bias)


class LogisticRegressionNewton:
    def __init__(self, n_iterations=15):
        self.n_iter = n_iterations; self.loss_history = []

    @staticmethod
    def _sigmoid(z):
        return 1 / (1 + np.exp(-np.clip(z, -500, 500)))

    def fit(self, X, y):
        n_samples, n_features = X.shape
        self.weights = np.zeros((n_features, 1)); self.bias = 0.0; self.loss_history = []
        for _ in range(self.n_iter):
            y_pred = self._sigmoid(np.dot(X, self.weights) + self.bias)
            self.loss_history.append(-np.mean(y * np.log(np.clip(y_pred, 1e-15, 1-1e-15)) + (1-y) * np.log(np.clip(1-y_pred, 1e-15, 1-1e-15))))
            grad_w = (1/n_samples) * np.dot(X.T, (y_pred - y))
            grad_b = (1/n_samples) * np.sum(y_pred - y)
            grad = np.vstack([grad_w, [[grad_b]]])
            S = np.diag((y_pred * (1 - y_pred)).flatten())
            X_aug = np.hstack([X, np.ones((n_samples, 1))])
            H = (1/n_samples) * np.dot(np.dot(X_aug.T, S), X_aug) + 1e-5 * np.eye(n_features + 1)
            try:
                delta = np.linalg.solve(H, grad)
            except:
                delta = np.linalg.lstsq(H, grad, rcond=None)[0]
            self.weights -= delta[:-1].reshape(-1, 1); self.bias -= delta[-1, 0]
        return self

    def predict(self, X):
        return (self._sigmoid(np.dot(X, self.weights) + self.bias) >= 0.5).astype(int)


def compute_metrics(y_true, y_pred):
    tp = int(np.sum((y_true == 1) & (y_pred == 1)))
    tn = int(np.sum((y_true == 0) & (y_pred == 0)))
    fp = int(np.sum((y_true == 0) & (y_pred == 1)))
    fn = int(np.sum((y_true == 1) & (y_pred == 0)))
    acc = (tp+tn)/len(y_true)
    prec = tp/(tp+fp) if (tp+fp)>0 else 0
    rec = tp/(tp+fn) if (tp+fn)>0 else 0
    f1 = 2*prec*rec/(prec+rec) if (prec+rec)>0 else 0
    return {'Accuracy':acc,'Precision':prec,'Recall':rec,'F1':f1,'TP':tp,'TN':tn,'FP':fp,'FN':fn}


# --- Image 1: feature distributions ---
fig, axes = plt.subplots(2, 3, figsize=(16, 10))
axes = axes.flatten()
feat_list = ['age', 'fare', 'sibsp', 'parch', 'sex', 'embarked_S']
titles = ['Возраст', 'Стоимость билета (Fare)', 'SibSp (родственники)',
          'Parch (родители/дети)', 'Пол (0=М, 1=Ж)', 'Embarked S (0/1)']
for i, (feat, title) in enumerate(zip(feat_list, titles)):
    if feat in ['sex', 'embarked_S']:
        cnt = features[feat].value_counts()
        axes[i].bar(['0', '1'], [cnt.get(0,0), cnt.get(1,0)], color=['#E74C3C','#2ECC71'], edgecolor='black')
        axes[i].set_title(title, fontsize=12, fontweight='bold')
        axes[i].set_ylabel('Количество')
    else:
        axes[i].hist(features[feat].dropna(), bins=25, color='#3498DB', edgecolor='black', alpha=0.85)
        axes[i].axvline(features[feat].mean(), color='red', ls='--', lw=2, label=f'Среднее: {features[feat].mean():.2f}')
        axes[i].axvline(features[feat].median(), color='green', ls='--', lw=2, label=f'Медиана: {features[feat].median():.2f}')
        axes[i].set_title(title, fontsize=12, fontweight='bold')
        axes[i].legend(fontsize=8); axes[i].set_ylabel('Частота')
axes[-1].set_visible(False)
plt.suptitle('Распределение признаков датасета Titanic', fontsize=15, fontweight='bold', y=1.02)
plt.tight_layout()
plt.savefig('/tmp/opencode/feature_distributions.png', dpi=150, bbox_inches='tight')
plt.close()
print('Saved: feature_distributions.png')

# --- Image 2: hyperparameter study ---
lr_list = [0.001, 0.01, 0.05, 0.1, 0.5, 1.0]
niter_list = [100, 500, 1000, 5000, 10000]
newton_list = [3, 5, 10, 15, 20, 30, 50]

gd_lr_results = []
for lr in lr_list:
    m = LogisticRegressionGD(learning_rate=lr, n_iterations=5000); m.fit(X_train, y_train)
    gd_lr_results.append({'lr':lr, **compute_metrics(y_test, m.predict(X_test))})

gd_iter_results = []
for n in niter_list:
    m = LogisticRegressionGD(learning_rate=0.1, n_iterations=n); m.fit(X_train, y_train)
    gd_iter_results.append({'n_iter':n, **compute_metrics(y_test, m.predict(X_test))})

newton_results = []
for n in newton_list:
    m = LogisticRegressionNewton(n_iterations=n); m.fit(X_train, y_train)
    newton_results.append({'n_iter':n, **compute_metrics(y_test, m.predict(X_test))})

fig, axes = plt.subplots(2, 2, figsize=(14, 10))
ax = axes[0,0]
for metric in ['Accuracy','Precision','Recall','F1']:
    ax.plot([r['lr'] for r in gd_lr_results], [r[metric] for r in gd_lr_results], marker='o', lw=2, label=metric, markersize=8)
ax.set_xscale('log'); ax.set_xlabel('Learning Rate (log scale)'); ax.set_ylabel('Значение метрики')
ax.set_title('Влияние learning rate (n_iter=5000)', fontweight='bold')
ax.legend(); ax.grid(True, alpha=0.3); ax.set_ylim(0.5, 1.0)

ax = axes[0,1]
for metric in ['Accuracy','Precision','Recall','F1']:
    ax.plot([r['n_iter'] for r in gd_iter_results], [r[metric] for r in gd_iter_results], marker='s', lw=2, label=metric, markersize=8)
ax.set_xscale('log'); ax.set_xlabel('Количество итераций (log scale)'); ax.set_ylabel('Значение метрики')
ax.set_title('Влияние количества итераций (lr=0.1)', fontweight='bold')
ax.legend(); ax.grid(True, alpha=0.3); ax.set_ylim(0.5, 1.0)

ax = axes[1,0]
for metric in ['Accuracy','Precision','Recall','F1']:
    ax.plot([r['n_iter'] for r in newton_results], [r[metric] for r in newton_results], marker='D', lw=2, label=metric, markersize=8)
ax.set_xlabel('Количество итераций'); ax.set_ylabel('Значение метрики')
ax.set_title('Метод Ньютона: влияние количества итераций', fontweight='bold')
ax.legend(); ax.grid(True, alpha=0.3); ax.set_ylim(0.5, 1.0)

ax = axes[1,1]
gd = LogisticRegressionGD(learning_rate=0.1, n_iterations=100); gd.fit(X_train, y_train)
nt = LogisticRegressionNewton(n_iterations=100); nt.fit(X_train, y_train)
ax.plot(gd.loss_history, label='Градиентный спуск (lr=0.1)', lw=2, color='#3498DB')
ax.plot(nt.loss_history, label='Метод Ньютона', lw=2, color='#E74C3C')
ax.set_xlabel('Итерация'); ax.set_ylabel('Log Loss')
ax.set_title('Сравнение сходимости: GD vs Newton', fontweight='bold')
ax.legend(); ax.grid(True, alpha=0.3)

plt.suptitle('Исследование гиперпараметров логистической регрессии', fontsize=15, fontweight='bold')
plt.tight_layout()
plt.savefig('/tmp/opencode/hyperparameter_study.png', dpi=150, bbox_inches='tight')
plt.close()
print('Saved: hyperparameter_study.png')

# --- Image 3: heatmap + summary ---
fig, axes = plt.subplots(1, 2, figsize=(14, 5))
heatmap_data = np.zeros((len(lr_list), len(niter_list)))
for i, lr in enumerate(lr_list):
    for j, n_iter in enumerate(niter_list):
        m = LogisticRegressionGD(learning_rate=lr, n_iterations=n_iter); m.fit(X_train, y_train)
        heatmap_data[i, j] = compute_metrics(y_test, m.predict(X_test))['Accuracy']

im = axes[0].imshow(heatmap_data, cmap='YlOrRd', aspect='auto', vmin=0.6, vmax=0.85)
axes[0].set_xticks(range(len(niter_list))); axes[0].set_xticklabels(niter_list)
axes[0].set_yticks(range(len(lr_list))); axes[0].set_yticklabels(lr_list)
axes[0].set_xlabel('Количество итераций'); axes[0].set_ylabel('Learning Rate')
axes[0].set_title('Accuracy (Градиентный спуск)', fontweight='bold')
for i in range(len(lr_list)):
    for j in range(len(niter_list)):
        axes[0].text(j, i, f'{heatmap_data[i,j]:.3f}', ha='center', va='center', fontsize=9, color='white' if heatmap_data[i,j]>0.75 else 'black')
plt.colorbar(im, ax=axes[0], shrink=0.8)

summary_data = {'Градиентный спуск': [max(r['Accuracy'] for r in gd_lr_results+gd_iter_results), max(r['Precision'] for r in gd_lr_results+gd_iter_results), max(r['Recall'] for r in gd_lr_results+gd_iter_results), max(r['F1'] for r in gd_lr_results+gd_iter_results)], 'Метод Ньютона': [max(r['Accuracy'] for r in newton_results), max(r['Precision'] for r in newton_results), max(r['Recall'] for r in newton_results), max(r['F1'] for r in newton_results)]}
axes[1].axis('off')
tbl = axes[1].table(cellText=[[f'{v:.4f}' for v in summary_data['Градиентный спуск']], [f'{v:.4f}' for v in summary_data['Метод Ньютона']]], rowLabels=['Градиентный спуск','Метод Ньютона'], colLabels=['Accuracy','Precision','Recall','F1-Score'], cellLoc='center', loc='center')
tbl.auto_set_font_size(False); tbl.set_fontsize(11); tbl.scale(1.2, 2.0)
for (row, col), cell in tbl.get_celld().items():
    if row == 0: cell.set_facecolor('#2C3E50'); cell.set_text_props(color='white', fontweight='bold')
axes[1].set_title('Сводка лучших результатов', fontweight='bold', y=1.05)
plt.tight_layout()
plt.savefig('/tmp/opencode/heatmap_summary.png', dpi=150, bbox_inches='tight')
plt.close()
print('Saved: heatmap_summary.png')

# --- Image 4: final model ---
best = LogisticRegressionGD(learning_rate=0.1, n_iterations=10000); best.fit(X_train, y_train)
m_best = compute_metrics(y_test, best.predict(X_test))
fig, axes = plt.subplots(1, 2, figsize=(14, 5))
axes[0].plot(best.loss_history, color='#2C3E50', lw=2)
axes[0].set_xlabel('Итерация'); axes[0].set_ylabel('Log Loss')
axes[0].set_title('Кривая обучения (Loss)', fontweight='bold')
axes[0].grid(True, alpha=0.3)
axes[0].axhline(y=best.loss_history[-1], color='red', ls='--', alpha=0.5, label=f'Loss: {best.loss_history[-1]:.4f}')
axes[0].legend()

cm = np.array([[m_best['TN'], m_best['FP']], [m_best['FN'], m_best['TP']]])
im = axes[1].imshow(cm, cmap='Blues', aspect='auto')
axes[1].set_xticks([0,1]); axes[1].set_xticklabels(['Предсказано: 0', 'Предсказано: 1'])
axes[1].set_yticks([0,1]); axes[1].set_yticklabels(['Реально: 0', 'Реально: 1'])
axes[1].set_title('Матрица ошибок', fontweight='bold')
for i in range(2):
    for j in range(2):
        axes[1].text(j, i, str(cm[i,j]), ha='center', va='center', fontsize=16, fontweight='bold', color='white' if cm[i,j]>cm.max()/2 else 'black')
plt.colorbar(im, ax=axes[1], shrink=0.8)
plt.suptitle('Финальная модель логистической регрессии', fontsize=14, fontweight='bold')
plt.tight_layout()
plt.savefig('/tmp/opencode/final_model.png', dpi=150, bbox_inches='tight')
plt.close()
print('Saved: final_model.png')

print('All images generated successfully.')
print(f'Best metrics: Acc={m_best["Accuracy"]:.4f}, Prec={m_best["Precision"]:.4f}, Rec={m_best["Recall"]:.4f}, F1={m_best["F1"]:.4f}')