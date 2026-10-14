
%Британская семья

person(george_vi).
person(elizabeth_bowes_lyon).
person(elizabeth_ii).
person(margaret).
person(philip).
person(charles_iii).
person(anne).
person(andrew).
person(edward).
person(diana).
person(camilla).
person(mark_phillips).
person(sarah_ferguson).
person(sophie).
person(william).
person(harry).
person(peter_phillips).
person(zara_tindall).
person(beatrice).
person(eugenie).
person(louise).
person(james).
person(catherine).
person(meghan).
person(george).
person(charlotte).
person(louis).
person(archie).
person(lilibet).
person(mike_tindall).

%Мужчина
male(george_vi).
male(philip).
male(charles_iii).
male(andrew).
male(edward).
male(mark_phillips).
male(william).
male(harry).
male(peter_phillips).
male(james).
male(george).
male(louis).
male(archie).
male(mike_tindall).

%Женщина
female(elizabeth_bowes_lyon).
female(elizabeth_ii).
female(margaret).
female(anne).
female(diana).
female(camilla).
female(sarah_ferguson).
female(sophie).
female(zara_tindall).
female(beatrice).
female(eugenie).
female(louise).
female(catherine).
female(meghan).
female(charlotte).
female(lilibet).


%Рождение
birth(george_vi, 1895).
birth(elizabeth_bowes_lyon, 1900).
birth(elizabeth_ii, 1926).
birth(margaret, 1930).
birth(philip, 1921).
birth(charles_iii, 1948).
birth(anne, 1950).
birth(andrew, 1960).
birth(edward, 1964).
birth(diana, 1961).
birth(camilla, 1947).
birth(mark_phillips, 1948).
birth(sarah_ferguson, 1959).
birth(sophie, 1965).
birth(william, 1982).
birth(harry, 1984).
birth(peter_phillips, 1977).
birth(zara_tindall, 1981).
birth(beatrice, 1988).
birth(eugenie, 1990).
birth(louise, 2003).
birth(james, 2007).
birth(catherine, 1982).
birth(meghan, 1981).
birth(george, 2013).
birth(charlotte, 2015).
birth(louis, 2018).
birth(archie, 2019).
birth(lilibet, 2021).
birth(mike_tindall, 1978).

%Смерть
death(george_vi, 1952).
death(elizabeth_bowes_lyon, 2002).
death(elizabeth_ii, 2022).
death(margaret, 2002).
death(philip, 2021).
death(diana, 1997).


%Свадьбы
marriage(george_vi, elizabeth_bowes_lyon, 1923).

marriage(elizabeth_ii, philip, 1947).

marriage(charles_iii, diana, 1981).
marriage(charles_iii, camilla, 2005).

marriage(anne, mark_phillips, 1973).

marriage(andrew, sarah_ferguson, 1986).

marriage(edward, sophie, 1999).

marriage(william, catherine, 2011).

marriage(harry, meghan, 2018).

marriage(zara_tindall, mike_tindall, 2011).


%Разводы
divorce(charles_iii, diana, 1996).
divorce(anne, mark_phillips, 1992).
divorce(andrew, sarah_ferguson, 1996).




% George VI + Elizabeth Bowes-Lyon

parent(george_vi, elizabeth_ii).
parent(elizabeth_bowes_lyon, elizabeth_ii).

parent(george_vi, margaret).
parent(elizabeth_bowes_lyon, margaret).


% Elizabeth II + Philip

parent(elizabeth_ii, charles_iii).
parent(philip, charles_iii).

parent(elizabeth_ii, anne).
parent(philip, anne).

parent(elizabeth_ii, andrew).
parent(philip, andrew).

parent(elizabeth_ii, edward).
parent(philip, edward).


% Charles + Diana

parent(charles_iii, william).
parent(diana, william).

parent(charles_iii, harry).
parent(diana, harry).


% Anne + Mark Phillips

parent(anne, peter_phillips).
parent(mark_phillips, peter_phillips).

parent(anne, zara_tindall).
parent(mark_phillips, zara_tindall).


% Andrew + Sarah

parent(andrew, beatrice).
parent(sarah_ferguson, beatrice).

parent(andrew, eugenie).
parent(sarah_ferguson, eugenie).


% Edward + Sophie

parent(edward, louise).
parent(sophie, louise).

parent(edward, james).
parent(sophie, james).


% William + Catherine

parent(william, george).
parent(catherine, george).

parent(william, charlotte).
parent(catherine, charlotte).

parent(william, louis).
parent(catherine, louis).


% Harry + Meghan

parent(harry, archie).
parent(meghan, archie).

parent(harry, lilibet).
parent(meghan, lilibet).


%Правила

% 1. Мать
mother(X, Y) :-
    female(X),
    parent(X, Y).


% 2. Отец
father(X, Y) :-
    male(X),
    parent(X, Y).


% 3. Ребёнок
child(X, Y) :-
    parent(Y, X).


% 4. Сын
son(X, Y) :-
    male(X),
    parent(Y, X).


% 5. Дочь
daughter(X, Y) :-
    female(X),
    parent(Y, X).


% 6. Братья/сёстры

sibling(X, Y) :-
    parent(P1, X),
    parent(P1, Y),
    parent(P2, X),
    parent(P2, Y),
    P1 @< P2,
    X \= Y.


% 7. Брат
brother(X, Y) :-
    male(X),
    sibling(X, Y).


% 8. Сестра
sister(X, Y) :-
    female(X),
    sibling(X, Y).


% 9. Дедушка или бабушка
grandparent(X, Y) :-
    parent(X, Z),
    parent(Z, Y).


% 10. Дедушка
grandfather(X, Y) :-
    male(X),
    grandparent(X, Y).


% 11. Бабушка
grandmother(X, Y) :-
    grandparent(X, Y),
    female(X).


% 12. Внук или внучка
grandchild(X, Y) :-
    grandparent(Y, X).


% 13. Внук
grandson(X, Y) :-
    male(X),
    grandparent(Y, X).


% 14. Внучка
granddaughter(X, Y) :-
    female(X),
    grandparent(Y, X).


% 15. Прадедушка или прабабушка
great_grandparent(X, Y) :-
    parent(X, Z),
    grandparent(Z, Y).


% 16. Прадедушка
great_grandfather(X, Y) :-
    male(X),
    great_grandparent(X, Y).


% 17. Прабабушка
great_grandmother(X, Y) :-
    female(X),
    great_grandparent(X, Y).


% 18. Предок
ancestor(X, Y) :-
    parent(X, Y).

ancestor(X, Y) :-
    parent(X, Z),
    ancestor(Z, Y).


% 19. Потомок
descendant(X, Y) :-
    ancestor(Y, X).


% 20. Дядя
uncle(X, Y) :-
    male(X),
    sibling(X, P),
    parent(P, Y).


% 21. Тётя
aunt(X, Y) :-
    female(X),
    sibling(X, P),
    parent(P, Y).


% 22. Племянник
nephew(X, Y) :-
    male(X),
    parent(P, X),
    sibling(P, Y).


% 23. Племянница
niece(X, Y) :-
    female(X),
    parent(P, X),
    sibling(P, Y).


% 24. Двоюродные братья/сёстры
cousin(X, Y) :-
    parent(P1, X),
    parent(P2, Y),
    sibling(P1, P2),
    X \= Y.


% 25. Супруг / супруга когда-либо
spouse(X, Y) :-
    marriage(X, Y, _).

spouse(X, Y) :-
    marriage(Y, X, _).


% 26. Бывший супруг
ex_spouse(X, Y) :-
    divorce(X, Y, _).

ex_spouse(X, Y) :-
    divorce(Y, X, _).


% 27. Был(а) женат / замужем
ever_married(X) :-
    marriage(X, _, _).

ever_married(X) :-
    marriage(_, X, _).


% 28. Разведён / разведена
divorced(X) :-
    divorce(X, _, _).

divorced(X) :-
    divorce(_, X, _).


% 29. Жив
alive(X) :-
    person(X),
    \+ death(X, _).


% 30. Умер
died(X) :-
    death(X, _).

% 31. Текущий супруг
% Брак существует, развода между этой парой нет, оба человека живы.

current_spouse(X, Y) :-
    spouse(X, Y),
    \+ ex_spouse(X, Y),
    alive(X),
    alive(Y).


% 32. Сейчас состоит в браке
married(X) :-
    current_spouse(X, _).


% 33. Никогда не состоял в браке
single(X) :-
    alive(X),
    \+ ever_married(X).


% 34. Вдовец / вдова
widowed(X) :-
    alive(X),
    spouse(X, Y),
    died(Y),
    \+ ex_spouse(X, Y).


% 35. Есть дети
has_children(X) :-
    parent(X, _).


% 36. Нет детей
childless(X) :-
    person(X),
    \+ parent(X, _).


% 37. Старше
older(X, Y) :-
    birth(X, BX),
    birth(Y, BY),
    BX < BY.


% 38. Младше
younger(X, Y) :-
    birth(X, BX),
    birth(Y, BY),
    BX > BY.
