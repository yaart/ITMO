:- encoding(utf8).

% ============================================================
% База знаний: британская королевская семья
% ============================================================

% ---------- Персоны: person(Id, РусскоеИмя, АнглийскаяМетка, Пол) ----------
person(elizabeth_ii, 'Елизавета II',          'Elizabeth II',                 f).
person(george_vi,    'Георг VI',              'George VI',                    m).
person(charles_iii,  'Чарльз III',            'Charles III',                  m).
person(anne,         'Принцесса Анна',        'Princess Anne',                f).
person(andrew,       'Принц Эндрю',           'Prince Andrew',                m).
person(edward,       'Принц Эдвард',          'Prince Edward',                m).
person(william,      'Принц Уильям',          'Prince William',               m).
person(harry,        'Принц Гарри',           'Prince Harry',                 m).
person(diana,        'Диана',                 'Diana, Princess of Wales',     f).
person(camilla,      'Камилла',               'Queen Camilla',                f).
person(catherine,    'Кейт',                  'Catherine, Princess of Wales', f).
person(meghan,       'Меган',                 'Meghan, Duchess of Sussex',    f).
person(george,       'Принц Джордж',          'Prince George',                m).
person(charlotte,    'Принцесса Шарлотта',    'Princess Charlotte',           f).
person(louis,        'Принц Луи',             'Prince Louis',                 m).
person(archie,       'Принц Арчи',            'Prince Archie',                m).
person(lilibet,      'Принцесса Лилибет',     'Princess Lilibet',             f).
person(margaret,     'Принцесса Маргарет',    'Princess Margaret',            f).
person(philip,       'Принц Филипп',          'Prince Philip',                m).
person(peter,        'Питер Филлипс',         'Peter Phillips',               m).
person(zara,         'Зара Тиндалл',          'Zara Tindall',                 f).
person(mark,         'Марк Филлипс',          'Mark Phillips',                m).
person(mike,         'Майк Тиндалл',          'Mike Tindall',                 m).
person(beatrice,     'Принцесса Беатрис',     'Princess Beatrice',            f).
person(eugenie,      'Принцесса Евгения',     'Princess Eugenie',             f).
person(sarah,        'Сара Фергюсон',         'Sarah Ferguson',               f).
person(sophie,       'Софи',                  'Sophie, Duchess of Edinburgh', f).
person(james,        'Джеймс',                'James, Earl of Wessex',        m).
person(louise,       'Леди Луиза',            'Lady Louise Windsor',          f).
person(ebl,          'Елизавета Боуз-Лайон',  'Elizabeth Bowes-Lyon',         f).

% ---------- Родительские связи (parent(Родитель, Ребёнок)) ----------
parent(elizabeth_ii, charles_iii).   parent(philip, charles_iii).
parent(elizabeth_ii, anne).          parent(philip, anne).
parent(elizabeth_ii, andrew).        parent(philip, andrew).
parent(elizabeth_ii, edward).        parent(philip, edward).
parent(george_vi, elizabeth_ii).     parent(ebl, elizabeth_ii).
parent(george_vi, margaret).         parent(ebl, margaret).
parent(charles_iii, william).        parent(diana, william).
parent(charles_iii, harry).          parent(diana, harry).
parent(william, george).             parent(catherine, george).
parent(william, charlotte).          parent(catherine, charlotte).
parent(william, louis).              parent(catherine, louis).
parent(harry, archie).               parent(meghan, archie).
parent(harry, lilibet).              parent(meghan, lilibet).
parent(anne, peter).                 parent(mark, peter).
parent(anne, zara).                  parent(mark, zara).
parent(andrew, beatrice).            parent(sarah, beatrice).
parent(andrew, eugenie).             parent(sarah, eugenie).
parent(edward, louise).              parent(sophie, louise).
parent(edward, james).               parent(sophie, james).

% ---------- Базовые супружеские связи (в одном направлении) ----------
spouse_raw(elizabeth_ii, philip).
spouse_raw(george_vi, ebl).
spouse_raw(charles_iii, diana).
spouse_raw(charles_iii, camilla).
spouse_raw(william, catherine).
spouse_raw(harry, meghan).
spouse_raw(anne, mark).
spouse_raw(zara, mike).
spouse_raw(andrew, sarah).
spouse_raw(edward, sophie).

% Симметричное отношение супружества
spouse(X, Y) :- spouse_raw(X, Y).
spouse(X, Y) :- spouse_raw(Y, X).

% ---------- Правила вывода ----------
father(X, Y) :- parent(X, Y), person(X, _, _, m).
mother(X, Y) :- parent(X, Y), person(X, _, _, f).

son(X, Y)    :- parent(Y, X), person(X, _, _, m).
daughter(X, Y) :- parent(Y, X), person(X, _, _, f).

brother(X, Y) :- parent(P, X), parent(P, Y), X \= Y, person(X, _, _, m).
sister(X, Y)  :- parent(P, X), parent(P, Y), X \= Y, person(X, _, _, f).

grandparent(X, Y) :- parent(X, Z), parent(Z, Y).
grandfather(X, Y) :- grandparent(X, Y), person(X, _, _, m).
grandmother(X, Y) :- grandparent(X, Y), person(X, _, _, f).

% Рекурсивный предок
ancestor(X, Y) :- parent(X, Y).
ancestor(X, Y) :- parent(X, Z), ancestor(Z, Y).

descendant(X, Y) :- ancestor(Y, X).

% ---------- Синонимы имён (все в нижнем регистре!) ----------
name_form('елизавета ii', elizabeth_ii).
name_form('елизаветы ii', elizabeth_ii).
name_form('елизавете ii', elizabeth_ii).
name_form('елизавету ii', elizabeth_ii).
name_form('елизаветой ii', elizabeth_ii).
name_form('королева елизавета', elizabeth_ii).
name_form('лизавета ii', elizabeth_ii).
name_form('лизаветы ii', elizabeth_ii).

name_form('георг vi', george_vi).
name_form('георга vi', george_vi).
name_form('георгу vi', george_vi).
name_form('георгом vi', george_vi).

name_form('чарльз iii', charles_iii).
name_form('чарльза iii', charles_iii).
name_form('чарльзу iii', charles_iii).
name_form('чарльзом iii', charles_iii).
name_form('чарльз', charles_iii).
name_form('чарльза', charles_iii).
name_form('карл iii', charles_iii).
name_form('карла iii', charles_iii).

name_form('анна', anne).
name_form('анны', anne).
name_form('анне', anne).
name_form('анну', anne).
name_form('анной', anne).
name_form('принцесса анна', anne).
name_form('принцессы анны', anne).
name_form('принцессу анну', anne).

name_form('эндрю', andrew).
name_form('эндрю', andrew).
name_form('принц эндрю', andrew).
name_form('принца эндрю', andrew).

name_form('эдвард', edward).
name_form('эдварда', edward).
name_form('эдварду', edward).
name_form('принц эдвард', edward).
name_form('принца эдварда', edward).

name_form('уильям', william).
name_form('уильяма', william).
name_form('уильяму', william).
name_form('уильямом', william).
name_form('вильям', william).
name_form('принц уильям', william).
name_form('принца уильяма', william).
name_form('принцу уильяму', william).

name_form('гарри', harry).
name_form('принц гарри', harry).
name_form('принца гарри', harry).

name_form('диана', diana).
name_form('дианы', diana).
name_form('диане', diana).
name_form('диану', diana).
name_form('дианой', diana).
name_form('принцесса диана', diana).
name_form('принцессы дианы', diana).

name_form('камилла', camilla).
name_form('камиллы', camilla).
name_form('камилле', camilla).
name_form('камиллу', camilla).
name_form('королева камилла', camilla).

name_form('кейт', catherine).
name_form('кетрин', catherine).
name_form('кэтрин', catherine).
name_form('кейт', catherine).
name_form('кетрин принцесса уэльская', catherine).

name_form('меган', meghan).
name_form('меган', meghan).

name_form('джордж', george).
name_form('джорджа', george).
name_form('джорджу', george).
name_form('принц джордж', george).
name_form('принца джорджа', george).

name_form('шарлотта', charlotte).
name_form('шарлотты', charlotte).
name_form('шарлотте', charlotte).
name_form('шарлотту', charlotte).
name_form('принцесса шарлотта', charlotte).
name_form('принцессы шарлотты', charlotte).

name_form('луи', louis).
name_form('принц луи', louis).

name_form('арчи', archie).
name_form('принц арчи', archie).

name_form('лилибет', lilibet).
name_form('принцесса лилибет', lilibet).

name_form('маргарет', margaret).
name_form('маргарет', margaret).
name_form('принцесса маргарет', margaret).
name_form('принцессы маргарет', margaret).

name_form('филипп', philip).
name_form('филиппа', philip).
name_form('филиппу', philip).
name_form('филиппом', philip).
name_form('филип', philip).
name_form('принц филипп', philip).
name_form('принца филиппа', philip).

name_form('питер', peter).
name_form('питера', peter).
name_form('питеру', peter).
name_form('питер филлипс', peter).

name_form('зара', zara).
name_form('зары', zara).
name_form('заре', zara).
name_form('зару', zara).
name_form('зара тиндалл', zara).

name_form('марк', mark).
name_form('марка', mark).
name_form('марку', mark).
name_form('марк филлипс', mark).

name_form('майк', mike).
name_form('майка', mike).
name_form('майку', mike).
name_form('майк тиндалл', mike).

name_form('беатрис', beatrice).
name_form('беатрис', beatrice).
name_form('принцесса беатрис', beatrice).
name_form('принцессы беатрис', beatrice).

name_form('евгения', eugenie).
name_form('евгении', eugenie).
name_form('евгению', eugenie).
name_form('принцесса евгения', eugenie).
name_form('принцессы евгении', eugenie).

name_form('сара', sarah).
name_form('сары', sarah).
name_form('саре', sarah).
name_form('сару', sarah).
name_form('сара фергюсон', sarah).

name_form('софи', sophie).
name_form('софия', sophie).
name_form('софии', sophie).

name_form('джеймс', james).
name_form('джеймса', james).
name_form('джеймсу', james).

name_form('луиза', louise).
name_form('луизы', louise).
name_form('луизе', louise).
name_form('луизу', louise).
name_form('леди луиза', louise).
name_form('леди луизы', louise).

name_form('элизабет боуз-лайон', ebl).
name_form('елизавета боуз-лайон', ebl).
name_form('королева мать', ebl).
name_form('королевы матери', ebl).

% ---------- Словарь отношений ----------
relation('детей',     children,    any).
relation('дети',      children,    any).
relation('ребёнок',   children,    any).
relation('ребенок',   children,    any).
relation('сыновей',   children,    m).
relation('сыновья',   children,    m).
relation('сын',       children,    m).
relation('сына',      children,    m).
relation('сыну',      children,    m).
relation('дочерей',   children,    f).
relation('дочери',    children,    f).
relation('дочь',      children,    f).
relation('дочку',     children,    f).
relation('родителей', parents,     any).
relation('родители',  parents,     any).
relation('мать',      parents,     f).
relation('матери',    parents,     f).
relation('матерью',   parents,     f).
relation('маму',      parents,     f).
relation('мама',      parents,     f).
relation('отец',      parents,     m).
relation('отца',      parents,     m).
relation('отцом',     parents,     m).
relation('отцу',      parents,     m).
relation('папа',      parents,     m).
relation('братьев',   siblings,    m).
relation('братья',    siblings,    m).
relation('брат',      siblings,    m).
relation('брата',     siblings,    m).
relation('брату',     siblings,    m).
relation('сестер',    siblings,    f).
relation('сестёр',    siblings,    f).
relation('сестры',    siblings,    f).
relation('сестра',    siblings,    f).
relation('сестру',    siblings,    f).
relation('супругов',  spouses,     any).
relation('супруги',   spouses,     any).
relation('муж',       spouses,     m).
relation('мужа',      spouses,     m).
relation('мужу',      spouses,     m).
relation('жена',      spouses,     f).
relation('жену',      spouses,     f).
relation('жены',      spouses,     f).
relation('потомков',  descendants, any).
relation('потомки',   descendants, any).
relation('предков',   ancestors,   any).
relation('предки',    ancestors,   any).

% ---------- Хелпер: проверка пола ----------
matches_gender(any, _).
matches_gender(G, G) :- G \= any.

% ---------- Хелпер: удаление дубликатов ----------
dedup(List, Result) :- list_to_set(List, Result).

% ---------- Поиск родственников (с дедупликацией) ----------
find_relatives(Id, children, G, Result) :-
    findall(C, (parent(Id, C), person(C, _, _, PG), matches_gender(G, PG)), L),
    dedup(L, Result).

find_relatives(Id, parents, G, Result) :-
    findall(P, (parent(P, Id), person(P, _, _, PG), matches_gender(G, PG)), L),
    dedup(L, Result).

find_relatives(Id, siblings, G, Result) :-
    findall(S, (parent(P, Id), parent(P, S), S \= Id,
                person(S, _, _, PG), matches_gender(G, PG)), L),
    dedup(L, Result).

find_relatives(Id, spouses, G, Result) :-
    findall(S, (spouse(Id, S), person(S, _, _, PG), matches_gender(G, PG)), L),
    dedup(L, Result).

find_relatives(Id, descendants, G, Result) :-
    findall(D, (descendant(D, Id), person(D, _, _, PG), matches_gender(G, PG)), L),
    dedup(L, Result).

find_relatives(Id, ancestors, G, Result) :-
    findall(A, (ancestor(A, Id), person(A, _, _, PG), matches_gender(G, PG)), L),
    dedup(L, Result).

% ---------- Кем является P1 для P2 ----------
what_is(P1, P2, 'мать')    :- parent(P1, P2), person(P1, _, _, f), !.
what_is(P1, P2, 'отец')    :- parent(P1, P2), person(P1, _, _, m), !.
what_is(P1, P2, 'дочь')    :- parent(P2, P1), person(P1, _, _, f), !.
what_is(P1, P2, 'сын')     :- parent(P2, P1), person(P1, _, _, m), !.
what_is(P1, P2, 'сестра')  :- parent(PP, P1), parent(PP, P2), P1 \= P2, person(P1, _, _, f), !.
what_is(P1, P2, 'брат')    :- parent(PP, P1), parent(PP, P2), P1 \= P2, person(P1, _, _, m), !.
what_is(P1, P2, 'супруга') :- spouse(P1, P2), person(P1, _, _, f), !.
what_is(P1, P2, 'супруг')  :- spouse(P1, P2), person(P1, _, _, m), !.
what_is(P1, P2, 'предок')  :- ancestor(P1, P2), !.
what_is(P1, P2, 'потомок') :- ancestor(P2, P1), !.
what_is(_, _, 'none').