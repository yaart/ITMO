import re
from pyswip import Prolog
import pymorphy3

# Инициализация Prolog и морфологического анализатора
prolog = Prolog()
prolog.consult("royal_family.pl")
morph = pymorphy3.MorphAnalyzer()

# ---------- Вспомогательные функции ----------
def to_str(x):
    """pyswip возвращает атомы как bytes или str — приводим к str."""
    if isinstance(x, bytes):
        return x.decode('utf-8')
    return str(x)

def normalize_word(word):
    """Приводит слово к нормальной форме (для русского)."""
    w = word.lower().strip()
    if w in ('i', 'ii', 'iii', 'iv', 'v', 'vi'):
        return w
    if not re.search(r'[а-яё]', w):
        return w
    return morph.parse(w)[0].normal_form

# ---------- Поиск персоны ----------
def find_person(name_words):
    """
    Ищет персону по последовательности слов.
    Пробует:
      1. Как введено (в нижнем регистре).
      2. Нормализованные формы слов.
    """
    raw = ' '.join(w.lower() for w in name_words)
    norm = ' '.join(normalize_word(w) for w in name_words)

    for candidate in (raw, norm):
        try:
            q = f"name_form('{candidate}', Id)"
            results = list(prolog.query(q))
            if results:
                return results[0]['Id']
        except Exception:
            pass
    return None

def get_label(person_id, lang='ru'):
    """Возвращает русскую или английскую метку."""
    if lang == 'ru':
        q = f"person({person_id}, Label, _, _)"
    else:
        q = f"person({person_id}, _, Label, _)"
    res = list(prolog.query(q))
    return to_str(res[0]['Label']) if res else to_str(person_id)

# ---------- Парсинг запроса ----------
def parse_query(text):
    """
    Возвращает (action, arg1, arg2), где
    action='rel'  → arg1 = слово-отношение, arg2 = список слов имени
    action='what' → arg1 = имя1, arg2 = имя2
    """
    low = text.strip().lower()

    # "кем является X для Y"
    m = re.match(r'^(?:найди\s+)?кем\s+является\s+(.+?)\s+для\s+(.+)$', low)
    if m:
        return ('what', m.group(1).split(), m.group(2).split())

    # "найди кто является <rel> <name>" / "кто <rel> <name>"
    m = re.match(r'^(?:найди\s+)?кто\s+(?:является\s+)?(\S+)\s+(.+)$', low)
    if m:
        return ('rel', m.group(1), m.group(2).split())

    # "найди <rel> <name>"
    m = re.match(r'^(?:найди\s+)?(\S+)\s+(.+)$', low)
    if m:
        return ('rel', m.group(1), m.group(2).split())

    return (None, None, None)

# ---------- Обработчик отношений ----------
def handle_relation(rel_word, name_words):
    rel_norm = normalize_word(rel_word)

    # Ищем отношение в Prolog (пробуем как есть и нормализованное)
    rel_type, gender = None, None
    for w in (rel_word, rel_norm):
        q = f"relation('{w}', T, G)"
        res = list(prolog.query(q))
        if res:
            rel_type = to_str(res[0]['T'])
            g = res[0]['G']
            gender = to_str(g) if not isinstance(g, str) or g != '_' else None
            # pyswip может вернуть '_' как Atom — обработаем
            if str(g) in ('_', 'None', 'none'):
                gender = None
            break

    if rel_type is None:
        print(f"Неизвестное отношение: '{rel_word}'")
        print("Доступные: детей, сыновей, дочерей, родителей, мать, отец,")
        print("           братьев, сестер, супругов, потомков, предков")
        return

    person_id = find_person(name_words)
    if not person_id:
        print(f"Персона '{' '.join(name_words)}' не найдена в базе.")
        return

    # Формируем Prolog-запрос find_relatives
    g_atom = gender if gender else '_'
    q = f"find_relatives({person_id}, {rel_type}, {g_atom}, Result)"
    res = list(prolog.query(q))
    relatives = res[0]['Result'] if res else []

    label = get_label(person_id)
    if not relatives:
        print(f"У {label} нет таких родственников в базе.")
    else:
        print(f"{rel_word.capitalize()} {label}:")
        for r in relatives:
            print(f"  - {get_label(r)}")

# ---------- Обработчик "кем является" ----------
def handle_what(name1_words, name2_words):
    p1 = find_person(name1_words)
    p2 = find_person(name2_words)

    if not p1:
        print(f"Персона '{' '.join(name1_words)}' не найдена.")
        return
    if not p2:
        print(f"Персона '{' '.join(name2_words)}' не найдена.")
        return

    q = f"what_is({p1}, {p2}, Rel)"
    res = list(prolog.query(q))
    if not res:
        print(f"Не удалось определить отношение.")
        return

    rel = to_str(res[0]['Rel'])
    if rel == 'none':
        print(f"{get_label(p1)} и {get_label(p2)} не связаны напрямую.")
    else:
        print(f"{get_label(p1)} является {rel} для {get_label(p2)}.")

# ---------- Справка ----------
def print_help():
    print("=" * 60)
    print(" Система рекомендаций: британская королевская семья")
    print("=" * 60)
    print("Примеры запросов:")
    print("  найди детей Елизаветы II")
    print("  найди братьев Уильяма")
    print("  найди кто является матерью Чарльза III")
    print("  найди супругов Елизаветы II")
    print("  найди потомков Георга VI")
    print("  найди предков Елизаветы II")
    print("  найди кем является Елизавета II для Чарльза III")
    print()
    print("Команды:")
    print("  помощь / help  — это сообщение")
    print("  выход / exit   — завершить")
    print()

# ---------- Главный цикл ----------
def main():
    print_help()

    while True:
        try:
            text = input("> ").strip()
        except (EOFError, KeyboardInterrupt):
            print("\nВыход.")
            break

        if not text:
            continue

        low = text.lower()
        if low in ('выход', 'exit', 'quit'):
            print("Выход.")
            break
        if low in ('помощь', 'help', '?'):
            print_help()
            continue

        action, arg1, arg2 = parse_query(text)

        if action == 'rel':
            handle_relation(arg1, arg2)
        elif action == 'what':
            handle_what(arg1, arg2)
        else:
            print("Не понял запрос. Введите 'помощь' для примеров.")
        print()

if __name__ == "__main__":
    main()