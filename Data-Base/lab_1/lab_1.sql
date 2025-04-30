drop table if exists Radiogram cascade;

drop table if exists Station cascade;
drop table if exists Employee cascade;
drop table if exists EAL cascade;
drop table if exists Organization cascade;

drop table if exists RadiogramType cascade;
drop table if exists StationType cascade;
drop table if exists EmployeePosition cascade;
drop table if exists OrganizationType cascade;
drop table if exists ProcessingStatus cascade;

CREATE TABLE Radiogram (
    radiogram_id INT PRIMARY KEY,
    content TEXT NOT NULL,
    reception_date_time TIMESTAMP NOT NULL,
    organization_id INT NOT NULL,
    station_id INT NOT NULL,
    processing_status_id INT NOT NULL,
    radiogram_type_id INT NOT NULL,
    priority VARCHAR(20) NOT NULL CHECK (priority IN ('Низкий', 'Средний', 'Высокий')),
    employee_id INT NOT NULL,
    eal_id INT NOT NULL
);

CREATE TABLE Station (
           station_id INT PRIMARY KEY,
           station_name VARCHAR(50) NOT NULL UNIQUE,
           location VARCHAR(100) NOT NULL,
           station_type_id INT NOT NULL,
           status VARCHAR(20) NOT NULL,
           commissioning_date DATE,
           contact_person VARCHAR(50) NOT NULL
);

CREATE TABLE Employee (
    employee_id INT PRIMARY KEY ,
    employee_name VARCHAR(50) NOT NULL,
    station_id INT NOT NULL,
    position_id INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('Активен', 'В отпуске', 'Уволен')),
    access_level VARCHAR(20) NOT NULL CHECK (access_level IN ('Низкий', 'Средний', 'Высокий'))

);

CREATE TABLE EAL (
    eal_id INT PRIMARY KEY,
    model VARCHAR(50) NOT NULL UNIQUE,
    station_id INT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('Активен', 'Неактивен', 'На обслуживании')),
    installation_date DATE NOT NULL,
    ai_level VARCHAR(30) NOT NULL CHECK (ai_level IN ('Базовый', 'Средний', 'Продвинутый'))
);

CREATE TABLE Organization (
           organization_id INT PRIMARY KEY,
           organization_name VARCHAR(50) NOT NULL,
           address VARCHAR(100) NOT NULL UNIQUE,
           organization_type_id INT NOT NULL,
           contact_person VARCHAR(50) NOT NULL,
           phone VARCHAR(30) NOT NULL UNIQUE CHECK (phone ~ '^\+7 \(\d{3}\) \d{3}-\d{2}-\d{2}$'),
    email VARCHAR(40) NOT NULL UNIQUE CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
);

CREATE TABLE RadiogramType (
            radiogram_type_id INT PRIMARY KEY,
            radiogram_type_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE StationType (
            station_type_id INT PRIMARY KEY,
            station_type_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE EmployeePosition (
            position_id INT PRIMARY KEY,
            position_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE OrganizationType (
            organization_type_id INT PRIMARY KEY,
            organization_type_name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE ProcessingStatus (
    processing_status_id INT PRIMARY KEY,
    processing_status_name VARCHAR(30) NOT NULL UNIQUE
);

ALTER TABLE Radiogram ADD FOREIGN KEY (organization_id) REFERENCES Organization(organization_id);

ALTER TABLE Radiogram ADD FOREIGN KEY (station_id) REFERENCES Station(station_id);

ALTER TABLE Radiogram ADD FOREIGN KEY (processing_status_id) REFERENCES ProcessingStatus(processing_status_id);

ALTER TABLE Radiogram ADD FOREIGN KEY (radiogram_type_id) REFERENCES RadiogramType(radiogram_type_id);

ALTER TABLE Radiogram ADD FOREIGN KEY (employee_id) REFERENCES Employee(employee_id);

ALTER TABLE Radiogram ADD CONSTRAINT fk_eal FOREIGN KEY (eal_id) REFERENCES EAL(eal_id);

ALTER TABLE Employee ADD FOREIGN KEY (station_id) REFERENCES Station(station_id);

ALTER TABLE Employee ADD FOREIGN KEY (position_id) REFERENCES EmployeePosition(position_id);

ALTER TABLE EAL ADD FOREIGN KEY (station_id) REFERENCES Station(station_id);

ALTER TABLE Organization ADD FOREIGN KEY (organization_type_id) REFERENCES OrganizationType(organization_type_id);

ALTER TABLE Station ADD FOREIGN KEY (station_type_id) REFERENCES StationType(station_type_id);

INSERT INTO RadiogramType (radiogram_type_id, radiogram_type_name) VALUES
(1, 'Обычная'),
(2, 'Срочная'),
(3, 'Секретная');

INSERT INTO StationType (station_type_id, station_type_name) VALUES
(1, 'Приемная'),
(2, 'Передающая'),
(3, 'Ретрансляционная');

INSERT INTO EmployeePosition (position_id, position_name) VALUES
(1, 'Оператор'),
(2, 'Инженер'),
(3, 'Администратор');

INSERT INTO OrganizationType (organization_type_id, organization_type_name) VALUES
(1, 'Государственная'),
(2, 'Частная'),
(3, 'Военная');

INSERT INTO ProcessingStatus (processing_status_id, processing_status_name) VALUES
(1, 'Получена'),
(2, 'В обработке'),
(3, 'Обработана');



INSERT INTO Station (station_id, station_name, location, station_type_id, status, commissioning_date, contact_person) VALUES
(1, 'Станция Альфа', 'Санкт-Петербург, Вяземский пер. 5-7', 1, 'Активна', '2025-01-15', 'Битконов Генадий'),
(2, 'Станция Бета', 'Санкт-Петербург, ул. Ломоносова 9', 2, 'Активна', '2022-05-10', 'Допсятин Алексей'),
(3, 'Станция Гамма', 'Новосибирск, Кронверский пр. 49', 3, 'Неактивна', '2024-03-22', 'Псыж Владимир');


INSERT INTO Organization (organization_id, organization_name, address, organization_type_id, contact_person, phone, email) VALUES
(1, 'Роскомнадзор', 'Москва, ул. Тверская, 15', 1, 'Иванова Мария', '+7 (495) 123-45-67', 'info@roscomnadzor.ru'),
(2, 'ТехноЛаб', 'Санкт-Петербург, ул. Мира, 10', 2, 'Петрова Анна', '+7 (812) 987-65-43', 'info@technolab.ru'),
(3, 'ВоенКом', 'Новосибирск, ул. Ленина, 25', 3, 'Сидоров Сергей', '+7 (383) 456-78-90', 'info@voenkom.ru');

INSERT INTO EAL (eal_id, model, station_id, status, installation_date, ai_level) VALUES
(1, 'DeepSeek', 1, 'Активен', '2024-04-15', 'Продвинутый'),
(2, 'ChatGPT', 2, 'Активен', '2023-03-16', 'Базовый'),
(3, 'GROK', 3, 'Неактивен', '2024-02-11', 'Средний');

INSERT INTO Employee (employee_id, employee_name, station_id, position_id, status, access_level) VALUES
(1, 'Смирнов Андрей', 1, 1, 'Активен', 'Высокий'),
(2, 'Кузнецова Ольга', 2, 2, 'В отпуске', 'Средний'),
(3, 'Васильев Дмитрий', 3, 3, 'Активен', 'Низкий'),
(4, 'Петрова Светлана', 1, 2, 'Активен', 'Средний'),
(5, 'Сидоров Алексей', 2, 1, 'Уволен', 'Низкий'),
(6, 'Федорова Анна', 3, 3, 'Активен', 'Высокий'),
(7, 'Григорьев Константин', 1, 1, 'В отпуске', 'Средний'),
(8, 'Куликова Мария', 2, 2, 'Активен', 'Высокий'),
(9, 'Тихонов Игорь', 3, 3, 'Активен', 'Низкий');

INSERT INTO Radiogram (radiogram_id, content, reception_date_time, organization_id, station_id, processing_status_id, radiogram_type_id, priority, employee_id, eal_id) VALUES
(1, 'Срочное сообщение от Роскомнадзора', '2025-10-01 10:00:52', 1, 1, 1, 1, 'Высокий', 1, 1),
(2, 'Техническое задание от ТехноЛаб', '2025-10-02 14:30:33', 2, 2, 2, 2, 'Средний', 2, 2),
(3, 'Секретное сообщение от ВоенКом', '2025-10-03 09:15:00', 3, 3, 3, 3, 'Средний', 3, 3),
(4, 'Запрос на информацию от Роскомнадзора', '2025-10-04 11:00:00', 1, 1, 1, 1, 'Низкий', 4, 1),
(5, 'Отчет о выполнении заданий от ТехноЛаб', '2025-10-05 15:45:00', 2, 2, 2, 2, 'Средний', 5, 2),
(6, 'Секретный отчет от ВоенКом', '2025-10-06 08:30:00', 3, 3, 3, 3, 'Высокий', 6, 3),
(7, 'План мероприятий от Роскомнадзора', '2025-10-07 12:00:00', 1, 1, 1, 1, 'Средний', 7, 1),
(8, 'Предложение по сотрудничеству от ТехноЛаб', '2025-10-08 14:15:00', 2, 2, 2, 2, 'Низкий', 8, 2),
(9, 'Запрос на техническую поддержку от ВоенКом', '2025-10-09 09:00:00', 3, 2, 3, 3, 'Высокий', 9, 3);


SELECT
    E.employee_id,
    E.employee_name,
    P.position_name,
    S.station_name,
    E.status
FROM Employee E
JOIN EmployeePosition P ON E.position_id = P.position_id
JOIN Station S ON E.station_id = S.station_id;

SELECT status, COUNT(*) AS count FROM Employee
GROUP BY status;

select * from Employee where station_id = 2;