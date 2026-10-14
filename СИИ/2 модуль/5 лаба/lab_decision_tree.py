"""ЛР5: многоветвевое ID3, метрики и SVG-графики. Только стандартная библиотека."""
import csv
import hashlib
import json
import math
import random
from dataclasses import dataclass, field, asdict
from pathlib import Path
from urllib.request import urlopen

BASE = Path(__file__).resolve().parent
URL = ('https://archive.ics.uci.edu/static/public/73/'
       'mushroom.zip')
FEATURES = (
    'cap-shape', 'cap-surface', 'cap-color', 'bruises', 'odor',
    'gill-attachment', 'gill-spacing', 'gill-size', 'gill-color',
    'stalk-shape', 'stalk-root', 'stalk-surface-above-ring',
    'stalk-surface-below-ring', 'stalk-color-above-ring',
    'stalk-color-below-ring', 'veil-type', 'veil-color',
    'ring-number', 'ring-type', 'spore-print-color',
    'population', 'habitat',
)
SEED = 42


def load_data():
    path = BASE / 'data' / 'agaricus-lepiota.data'
    if not path.exists():
        from io import BytesIO
        from zipfile import ZipFile
        path.parent.mkdir(parents=True, exist_ok=True)
        with urlopen(URL, timeout=60) as response:
            archive = ZipFile(BytesIO(response.read()))
        for name in ('agaricus-lepiota.data', 'agaricus-lepiota.names'):
            (path.parent / name).write_bytes(archive.read(name))
    with path.open(encoding='ascii', newline='') as source:
        rows = [tuple(row) for row in csv.reader(source)]
    if len(rows) != 8124 or any(len(r) != 23 for r in rows):
        raise ValueError('Неожиданный формат Mushroom')
    return [dict(zip(('class',) + FEATURES, r)) for r in rows]


def split_data(rows):
    """Стратифицированное разбиение 80/20 с независимым генератором."""
    rng = random.Random(SEED)
    train, test = [], []
    for label in ('e', 'p'):
        group = [i for i, r in enumerate(rows) if r['class'] == label]
        rng.shuffle(group)
        size = round(0.2 * len(group))
        test.extend(group[:size])
        train.extend(group[size:])
    rng.shuffle(train)
    rng.shuffle(test)
    return train, test


def entropy(rows):
    p = sum(r['class'] == 'p' for r in rows) / len(rows)
    return -sum(q * math.log2(q) for q in (p, 1 - p) if q > 0)


@dataclass
class Node:
    count: int
    probability: float
    feature: str | None = None
    children: dict = field(default_factory=dict)


def build_tree(rows, features):
    probability = sum(r['class'] == 'p' for r in rows) / len(rows)
    node = Node(len(rows), probability)
    if probability in (0, 1) or not features:
        return node
    best_gain, best_feature, best_groups = -math.inf, None, None
    for feature in features:
        groups = {}
        for row in rows:
            groups.setdefault(row[feature], []).append(row)
        if len(groups) < 2:
            continue
        gain = entropy(rows) - sum(
            len(group) / len(rows) * entropy(group)
            for group in groups.values())
        if gain > best_gain + 1e-12:
            best_gain, best_feature, best_groups = gain, feature, groups
    if best_feature is None:
        return node
    # Нулевой gain допускается: ниже может обнаружиться взаимодействие.
    node.feature = best_feature
    remaining = tuple(f for f in features if f != best_feature)
    node.children = {value: build_tree(group, remaining)
                     for value, group in sorted(best_groups.items())}
    return node


def predict_probability(node, row):
    while node.feature is not None:
        child = node.children.get(row.get(node.feature))
        if child is None:
            break  # Неизвестная категория: частота класса текущего узла.
        node = child
    return node.probability


def metrics(labels, scores, threshold=0.5):
    tp = fp = tn = fn = 0
    for actual, score in zip(labels, scores):
        positive = score >= threshold
        tp += int(actual == 1 and positive)
        fp += int(actual == 0 and positive)
        tn += int(actual == 0 and not positive)
        fn += int(actual == 1 and not positive)
    return dict(TP=tp, FP=fp, TN=tn, FN=fn,
                accuracy=(tp + tn) / len(labels),
                precision=tp / (tp + fp) if tp + fp else 0.0,
                recall=tp / (tp + fn) if tp + fn else 0.0)


def curves(labels, scores):
    """Порог убывает; объекты с равными score добавляются одновременно."""
    positives, negatives = sum(labels), len(labels) - sum(labels)
    if not positives or not negatives:
        raise ValueError('Для ROC нужны оба класса')
    pairs = sorted(zip(scores, labels), reverse=True)
    points = [dict(threshold='inf', fpr=0.0, tpr=0.0,
                   recall=0.0, precision=1.0)]
    tp = fp = i = 0
    while i < len(pairs):
        threshold = pairs[i][0]
        while i < len(pairs) and pairs[i][0] == threshold:
            tp += pairs[i][1]
            fp += 1 - pairs[i][1]
            i += 1
        points.append(dict(threshold=threshold, fpr=fp / negatives,
                           tpr=tp / positives, recall=tp / positives,
                           precision=tp / (tp + fp)))
    return points


def area(points, x, y):
    return sum((b[x] - a[x]) * (a[y] + b[y]) / 2
               for a, b in zip(points, points[1:]))


def write_curve(points, x, y, title, baseline, path):
    """SVG составляется вручную, без графических библиотек."""
    from html import escape
    elements = ['<svg xmlns="http://www.w3.org/2000/svg" '
                'width="960" height="640" viewBox="0 0 960 640">',
                '<rect width="960" height="640" fill="white"/>',
                '<g font-family="Arial" font-size="20" fill="#222">']
    def text(xp, yp, value, anchor='middle'):
        elements.append(f'<text x="{xp}" y="{yp}" text-anchor="{anchor}">'
                        f'{escape(value)}</text>')
    def line(x1, y1, x2, y2, color, width=1, dash=''):
        elements.append(f'<line x1="{x1}" y1="{y1}" x2="{x2}" '
                        f'y2="{y2}" stroke="{color}" stroke-width="{width}" '
                        f'stroke-dasharray="{dash}"/>')
    def xy(a, b):
        return 100 + 800 * a, 550 - 460 * b
    text(480, 38, title)
    for j in range(6):
        v = j / 5
        xp, yp = xy(v, v)
        line(xp, 90, xp, 550, '#dddddd')
        line(100, yp, 900, yp, '#dddddd')
        text(xp, 582, f'{v:.1f}')
        text(80, yp + 7, f'{v:.1f}', 'end')
    line(100, 90, 100, 550, '#222222', 2)
    line(100, 550, 900, 550, '#222222', 2)
    a, b = xy(0, 0 if baseline is None else baseline)
    c, d = xy(1, 1 if baseline is None else baseline)
    line(a, b, c, d, '#888888', 2, '8 6')
    coords = ' '.join(f'{a:.3f},{b:.3f}'
                      for a, b in (xy(p[x], p[y]) for p in points))
    elements.append(f'<polyline points="{coords}" fill="none" '
                    'stroke="#1f77b4" stroke-width="3"/>')
    text(500, 624, 'FPR' if x == 'fpr' else 'Recall')
    text(100, 72, 'TPR' if y == 'tpr' else 'Precision', 'start')
    text(870, 520, f'AUC = {area(points, x, y):.4f}', 'end')
    elements.append('</g></svg>')
    path.write_text('\n'.join(elements), encoding='utf-8')


def tree_stats(node):
    stats = [tree_stats(child) for child in node.children.values()]
    return dict(nodes=1 + sum(s['nodes'] for s in stats),
                leaves=sum(s['leaves'] for s in stats) if stats else 1,
                depth=1 + max(s['depth'] for s in stats) if stats else 0,
                max_children=max([len(node.children)] +
                                 [s['max_children'] for s in stats]))


def main():
    rows = load_data()
    selected = tuple(random.Random(SEED).sample(FEATURES, math.isqrt(len(FEATURES))))
    train_ids, test_ids = split_data(rows)
    train, test = [rows[i] for i in train_ids], [rows[i] for i in test_ids]
    tree = build_tree(train, selected)
    out = BASE / 'results'
    out.mkdir(exist_ok=True)
    results = dict(seed=SEED, selected_features=selected, samples=len(rows),
                   missing_stalk_root=sum(r['stalk-root'] == '?' for r in rows),
                   tree=tree_stats(tree), root=tree.feature)
    for name, data, ids in (('train', train, train_ids), ('test', test, test_ids)):
        labels = [int(r['class'] == 'p') for r in data]
        scores = [predict_probability(tree, r) for r in data]
        points = curves(labels, scores)
        results[name] = dict(size=len(data), positives=sum(labels),
                             **metrics(labels, scores),
                             roc_auc=area(points, 'fpr', 'tpr'),
                             pr_auc=area(points, 'recall', 'precision'))
        if name == 'test':
            with (out / 'predictions.csv').open('w', newline='') as f:
                writer = csv.writer(f)
                writer.writerow(('row_index', 'y_true', 'probability', 'y_pred'))
                writer.writerows((i, y, s, int(s >= 0.5))
                                 for i, y, s in zip(ids, labels, scores))
            with (out / 'curve_points.csv').open('w', newline='') as f:
                writer = csv.DictWriter(f, fieldnames=points[0].keys())
                writer.writeheader()
                writer.writerows(points)
            write_curve(points, 'fpr', 'tpr', 'ROC curve (test)',
                        None, out / 'roc.svg')
            write_curve(points, 'recall', 'precision', 'Precision-Recall (test)',
                        sum(labels) / len(labels), out / 'pr.svg')
    results['data_sha256'] = hashlib.sha256(
        (BASE / 'data' / 'agaricus-lepiota.data').read_bytes()).hexdigest()
    (out / 'metrics.json').write_text(json.dumps(results, indent=2), encoding='utf-8')
    (out / 'tree.json').write_text(json.dumps(asdict(tree), indent=2), encoding='utf-8')
    (out / 'split.json').write_text(json.dumps(dict(train=train_ids, test=test_ids)))
    print(json.dumps(results, indent=2))


if __name__ == '__main__':
    main()
