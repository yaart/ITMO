import re

print("-----------------------------ОСНОВНОЕ ЗАДАНИЕ-----------------------------------------------------------------")
print()


def find_smiles(text):
    pattern = r'8-{O'
    smiles_count = re.findall(pattern, text)
    return ('Количество смайликов', len(smiles_count))


tests_0 = ['тут ничего нет',
           '8-{Oleg optimizes 8-{Operations 8-{Often.',
           '88-{OO8-{{8{O8-{O--{O88--{{OO',
           '2reg38-{Dkp8-{Oc"askf#8-{0(asdf@jar8-{O',
           'анекдоты про ПСЖ не придумал 8-{O']

for test in tests_0:
    print(*find_smiles(test))

print()
print("-----------------------------ДОПОЛНИТЕЛЬНОЕ ЗАДАНИЕ 1----------------------------------------------------------")
print()


def is_haiku(text):
    pattern = r'^(?:[^/]+/){2}[^/]+$'
    is_three_sent = re.fullmatch(pattern, text.strip()) is None
    if is_three_sent:
        return "Не хайку. Должно быть 3 строки."

    syllable_counts = [len(re.findall(r'[аеёиоуыэюяaeiouy]', line.strip(), re.IGNORECASE)) for line in text.split("/")]
    if syllable_counts == [5, 7, 5]:
        return "Хайку!"
    else:
        return "Не хайку."


tests_1 = ["Листья шелестят / Ветер шепчет о прошлом / Осень за окном.",
           "Псевдо Хайку текст",
           "Как вишня расцвела! / Она с коня согнала / И князя-гордеца.",
           "Солнце встает // Тень исчезает",
           "Листья падают / Осень красит природу / Ветер шепчет вновь"]

for test in tests_1:
    print(is_haiku(test))

print()
print("-----------------------------ДОПОЛНИТЕЛЬНОЕ ЗАДАНИЕ 2-------------------------------------------------------")
print()


def replace_adj(padezh, text):
    end = ["ый", "ого", "ому", "ый", "ым", "ом"][padezh - 1]
    all_end_adj = "ый|ий|ой|ого|ем|ому|ему|ым|им|ом|ей|ая|яя|ой|ей|ую|юю|ые|ие|ых|их|ым|им|ыми|ими"

    adjective_pattern = rf'\b(\w+?)(?:{all_end_adj})\b'

    def replace_adjective(match):
        return (match.group(1) + end).upper()

    all_osnovi_adj = list(set(re.findall(adjective_pattern, text.lower())))
    repl_osn_adj = ""
    for adj in all_osnovi_adj:
        if text.lower().count(adj) >= 2:
            repl_osn_adj += adj + "|"
    return str(re.sub(rf'\b({repl_osn_adj[:-1]})(?:{all_end_adj})\b', replace_adjective, text.lower()))


tests_2 = [[6, "Красивая кошка сидела на красивом диване, а рядом лежала красивая игрушка."],
           [3, "Мы купили новые бархатные книги, и я выбрал новую классную для чтения, лежал на бархатном уютном одеяле"],
           [2, "Смешной анекдот заставил всех смеяться, и я рассказал его своему смешному другу."],
           [5, "В большом парке играли дети, а на большой площадке проходил праздник."],
           [4, "Я увидел светлую звезду на светлом небе и пожелал исполнить светлую мечту."]]

for padezh, text in tests_2:
    print(replace_adj(padezh, text))

dop_2 = "Футбольный клуб «Реал Мадрид» является 15-кратным обладателем главного футбольного \
европейского трофея – Лиги Чемпионов. Данный турнир организован Союзом европейских футбольных ассоциаций (УЕФА). \
Идея о континентальном футбольном турнире пришла к журналисту Габриэлю Ано в 1955 год"
print(replace_adj(2, dop_2))
