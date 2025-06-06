/* Задание 1
Сделать запрос для получения атрибутов из указанных таблиц, применив фильтры по указанным условиям:
Таблицы: Н_ОЦЕНКИ, Н_ВЕДОМОСТИ.
Вывести атрибуты: Н_ОЦЕНКИ.КОД, Н_ВЕДОМОСТИ.ЧЛВК_ИД.
Фильтры (AND):
a) Н_ОЦЕНКИ.ПРИМЕЧАНИЕ = незачет.
b) Н_ВЕДОМОСТИ.ДАТА > 1998-01-05.
Вид соединения: LEFT JOIN.
*/

SELECT DISTINCT "Н_ОЦЕНКИ"."КОД", "Н_ВЕДОМОСТИ"."ЧЛВК_ИД"
FROM "Н_ОЦЕНКИ"

LEFT JOIN "Н_ВЕДОМОСТИ" ON "Н_ОЦЕНКИ"."КОД" = "Н_ВЕДОМОСТИ"."ОЦЕНКА"

WHERE
    "Н_ОЦЕНКИ"."ПРИМЕЧАНИЕ" = 'незачет'
    AND "Н_ВЕДОМОСТИ"."ДАТА" > '1998-01-05';

/* Задание 2
Сделать запрос для получения атрибутов из указанных таблиц, применив фильтры по указанным условиям:
Таблицы: Н_ЛЮДИ, Н_ОБУЧЕНИЯ, Н_УЧЕНИКИ.
Вывести атрибуты: Н_ЛЮДИ.ИД, Н_ОБУЧЕНИЯ.НЗК, Н_УЧЕНИКИ.ГРУППА.
Фильтры: (AND)
a) Н_ЛЮДИ.ИМЯ < Роман.
b) Н_ОБУЧЕНИЯ.НЗК > 933232.
c) Н_УЧЕНИКИ.ИД > 100410.
Вид соединения: LEFT JOIN.
*/

SELECT DISTINCT
    "Н_ЛЮДИ"."ИД",
    "Н_ОБУЧЕНИЯ"."НЗК",
    "Н_УЧЕНИКИ"."ГРУППА"
FROM
    "Н_ЛЮДИ"

LEFT JOIN
        "Н_ОБУЧЕНИЯ" ON "Н_ЛЮДИ"."ИД" = "Н_ОБУЧЕНИЯ"."ЧЛВК_ИД"
LEFT JOIN
        "Н_УЧЕНИКИ" ON "Н_ЛЮДИ"."ИД" = "Н_УЧЕНИКИ"."ЧЛВК_ИД"

WHERE
    "Н_ЛЮДИ"."ИМЯ" < 'Роман'
    AND CAST("Н_ОБУЧЕНИЯ"."НЗК" AS INT) > 933232
    AND "Н_УЧЕНИКИ"."ИД" > 100410;


/* Задание 3
Вывести число студентов вечерней формы обучения, которые старше 25 лет.
Ответ должен содержать только одно число.
*/

SELECT COUNT(DISTINCT "Н_ЛЮДИ"."ИД") AS "Число студентов"
FROM "Н_ЛЮДИ"

JOIN "Н_УЧЕНИКИ" ON "Н_ЛЮДИ"."ИД" = "Н_УЧЕНИКИ"."ЧЛВК_ИД"
JOIN "Н_ПЛАНЫ" ON "Н_УЧЕНИКИ"."ПЛАН_ИД" = "Н_ПЛАНЫ"."ИД"
JOIN "Н_ФОРМЫ_ОБУЧЕНИЯ" ON "Н_ПЛАНЫ"."ФО_ИД" = "Н_ФОРМЫ_ОБУЧЕНИЯ"."ИД"

WHERE "Н_ФОРМЫ_ОБУЧЕНИЯ"."НАИМЕНОВАНИЕ" = 'Очно-заочная(вечерняя)'
  AND EXTRACT(YEAR FROM AGE("Н_ЛЮДИ"."ДАТА_РОЖДЕНИЯ")) > 25;


/* Задание 4
Выдать различные отчества преподавателей и число людей с каждой из этих отчеств, ограничив список отчествами,
встречающимися более 50 раз на заочной форме обучения.
Для реализации использовать соединение таблиц.
*/

WITH
-- Все отчества преподавателей (уникальные)
ПреподавательскиеОтчества AS (
    SELECT DISTINCT "Н_ЛЮДИ"."ОТЧЕСТВО"
    FROM "Н_ЛЮДИ"
    WHERE "Н_ЛЮДИ"."ОТЧЕСТВО" IS NOT NULL
    AND "Н_ЛЮДИ"."ИД" NOT IN (SELECT "ЧЛВК_ИД" FROM "Н_УЧЕНИКИ")  -- Люди, не являющиеся студентами
),

-- Количество заочников по каждому отчеству(заочников нет вообще)
ЗаочникиПоОтчествам AS (
    SELECT
        "Н_ЛЮДИ"."ОТЧЕСТВО",
        COUNT(*) AS "КоличествоЗаочников"
    FROM "Н_ЛЮДИ"
    JOIN "Н_УЧЕНИКИ" ON "Н_ЛЮДИ"."ИД" = "Н_УЧЕНИКИ"."ЧЛВК_ИД"
    JOIN "Н_ПЛАНЫ" ON "Н_УЧЕНИКИ"."ПЛАН_ИД" = "Н_ПЛАНЫ"."ИД"
    JOIN "Н_ФОРМЫ_ОБУЧЕНИЯ" ON "Н_ПЛАНЫ"."ФО_ИД" = "Н_ФОРМЫ_ОБУЧЕНИЯ"."ИД"
    WHERE "Н_ФОРМЫ_ОБУЧЕНИЯ"."НАИМЕНОВАНИЕ" = 'Очная'
    AND "Н_ЛЮДИ"."ОТЧЕСТВО" IS NOT NULL
    GROUP BY "Н_ЛЮДИ"."ОТЧЕСТВО"
    HAVING COUNT(*) > 50
)


-- Итоговый результат: отчества преподавателей и количество заочников с ними
SELECT
    п."ОТЧЕСТВО" AS "Отчество преподавателя",
    з."КоличествоЗаочников" AS "Число заочников с этим отчеством"
FROM
    ПреподавательскиеОтчества п
JOIN
    ЗаочникиПоОтчествам з ON п."ОТЧЕСТВО" = з."ОТЧЕСТВО"
ORDER BY
    з."КоличествоЗаочников" DESC;



----


/* Задание 5
Выведите таблицу со средними оценками студентов группы 4100 (Номер, ФИО, Ср_оценка),
у которых средняя оценка больше средней оценки в группе 3100.
*/
SELECT "Н_УЧЕНИКИ"."ИД",
       "Н_ЛЮДИ"."ФАМИЛИЯ",
       "Н_ЛЮДИ"."ИМЯ",
       "Н_ЛЮДИ"."ОТЧЕСТВО",
       ROUND(AVG(CAST("Н_ВЕДОМОСТИ"."ОЦЕНКА" AS INT)), 2) AS "СР_ОЦЕНКА"
FROM "Н_УЧЕНИКИ"
         INNER JOIN "Н_ЛЮДИ" on "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
         INNER JOIN "Н_ВЕДОМОСТИ" on "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ВЕДОМОСТИ"."ЧЛВК_ИД"
WHERE "Н_УЧЕНИКИ"."ГРУППА" = '4120' AND "Н_ВЕДОМОСТИ"."ОЦЕНКА" ~ '^[2-5]$'
GROUP BY "Н_УЧЕНИКИ"."ИД", "Н_ЛЮДИ"."ФАМИЛИЯ", "Н_ЛЮДИ"."ИМЯ", "Н_ЛЮДИ"."ОТЧЕСТВО"
HAVING ROUND(AVG(CAST("Н_ВЕДОМОСТИ"."ОЦЕНКА" AS INT)), 2) >
       ROUND((SELECT AVG(CAST("Н_ВЕДОМОСТИ"."ОЦЕНКА" AS INT))
              FROM "Н_УЧЕНИКИ"
                       INNER JOIN "Н_ВЕДОМОСТИ" ON "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ВЕДОМОСТИ"."ЧЛВК_ИД"
              WHERE "Н_УЧЕНИКИ"."ГРУППА" = '3100'
                AND "Н_ВЕДОМОСТИ"."ОЦЕНКА" ~ '^[2-5]$'), 2);

/* Задание 6
Получить список студентов, отчисленных после первого сентября 2012 года с заочной формы обучения. В результат включить:
номер группы;
номер, фамилию, имя и отчество студента;
номер пункта приказа;
Для реализации использовать подзапрос с IN.
*/

SELECT
    "Н_УЧЕНИКИ"."ГРУППА",
    "Н_ЛЮДИ"."ФАМИЛИЯ",
    "Н_ЛЮДИ"."ИМЯ",
    "Н_ЛЮДИ"."ОТЧЕСТВО",
    "Н_УЧЕНИКИ"."П_ПРКОК_ИД",
    "Н_УЧЕНИКИ"."КОНЕЦ"

FROM
    "Н_УЧЕНИКИ"
INNER JOIN
    "Н_ЛЮДИ" ON "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
INNER JOIN
    "Н_ПЛАНЫ" ON "Н_УЧЕНИКИ"."ПЛАН_ИД" = "Н_ПЛАНЫ"."ИД"
INNER JOIN
    "Н_ФОРМЫ_ОБУЧЕНИЯ" ON "Н_ПЛАНЫ"."ФО_ИД" = "Н_ФОРМЫ_ОБУЧЕНИЯ"."ИД"
WHERE
    "Н_ФОРМЫ_ОБУЧЕНИЯ"."НАИМЕНОВАНИЕ" = 'Очная'
    AND "Н_УЧЕНИКИ"."ИД" IN (
        SELECT
            "Н_УЧЕНИКИ"."ИД"
        FROM
            "Н_УЧЕНИКИ"
        WHERE
            "Н_УЧЕНИКИ"."ПРИЗНАК" = 'отчисл'
            AND "Н_УЧЕНИКИ"."СОСТОЯНИЕ" = 'утвержден'
            AND CAST("Н_УЧЕНИКИ"."КОНЕЦ" AS DATE) > '2012-09-01'
    );



/* Задание 7
Вывести список студентов, имеющих одинаковые фамилии, но не совпадающие ид.
*/

SELECT
    "Н_ЛЮДИ"."ИД",
    "Н_ЛЮДИ"."ФАМИЛИЯ",
    "Н_ЛЮДИ"."ИМЯ",
    "Н_ЛЮДИ"."ОТЧЕСТВО"
FROM
    "Н_ЛЮДИ"
WHERE
    "Н_ЛЮДИ"."ФАМИЛИЯ" IN (
        SELECT "ФАМИЛИЯ"
        FROM "Н_ЛЮДИ"
        GROUP BY "ФАМИЛИЯ"
        HAVING COUNT(*) > 1
    )
    AND EXISTS (
        SELECT 1
        FROM "Н_УЧЕНИКИ"
        WHERE "Н_УЧЕНИКИ"."ЧЛВК_ИД" = "Н_ЛЮДИ"."ИД"
    )
ORDER BY
    "Н_ЛЮДИ"."ФАМИЛИЯ", "Н_ЛЮДИ"."ИД";
