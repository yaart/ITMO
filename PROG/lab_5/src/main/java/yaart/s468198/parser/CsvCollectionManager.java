package yaart.s468198.parser;

import yaart.s468198.exceptions.DeserializationException;
import yaart.s468198.models.*;
import org.apache.commons.csv.*;
import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CsvCollectionManager - класс для работы с коллекцией объектов LabWork,
 * сохраняемой в формате CSV.
 *
 * Этот класс предоставляет методы для загрузки и сохранения коллекции LabWork
 * в файл CSV, а также вспомогательные методы для парсинга данных.
 */
public class CsvCollectionManager {
    /**
     * Путь к файлу CSV, используемому для хранения коллекции.
     */
    private final String filepath;

    /**
     * Форматтер даты и времени для сериализации/десериализации полей ZonedDateTime.
     */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_ZONED_DATE_TIME;

    /**
     * Конструктор класса.
     *
     * @param filename путь к файлу CSV
     */
    public CsvCollectionManager(String filename) {
        this.filepath = filename;
    }

    /**
     * Метод для сохранения коллекции LabWork в файл CSV.
     *
     * @param collection коллекция объектов LabWork для сохранения
     * @throws IOException если произошла ошибка записи файла
     */
    public void saveCollection(Collection<LabWork> collection) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filepath), "UTF-8");
             CSVPrinter printer = CSVFormat.DEFAULT
                     .withHeader(
                             "id", "name", "x", "y",
                             "creationDate", "minimalPoint", "tunedInWorks",
                             "difficulty", "disciplineName", "disciplineLectureHours",
                             "disciplinePracticeHours", "disciplineSelfStudyHours", "disciplineLabsCount"
                     )
                     .print(writer)) {
            // Сортируем коллекцию перед сохранением
            List<LabWork> sortedCollection = collection.stream()
                    .sorted() // Используем реализацию Comparable в LabWork
                    .collect(Collectors.toList());

            for (LabWork labWork : sortedCollection) {
                if (!labWork.validate()) {
                    throw new IOException("Некорректные данные LabWork (ID: " + labWork.getId() + ")");
                }
                printer.printRecord(
                        labWork.getId(),
                        labWork.getName(),
                        labWork.getCoordinates().getX(),
                        labWork.getCoordinates().getY(),
                        formatDate(labWork.getCreationDate()),
                        labWork.getMinimalPoint(),
                        labWork.getTunedInWorks(),
                        labWork.getDifficulty().name(),
                        getDisciplineField(labWork.getDiscipline(), Discipline::getName),
                        getDisciplineField(labWork.getDiscipline(), Discipline::getLectureHours),
                        getDisciplineField(labWork.getDiscipline(), Discipline::getPracticeHours),
                        getDisciplineField(labWork.getDiscipline(), d -> d.getSelfStudyHours()),
                        getDisciplineField(labWork.getDiscipline(), Discipline::getLabsCount)
                );
            }
        }
    }

    /**
     * Метод для загрузки коллекции LabWork из файла CSV.
     *
     * @return коллекция объектов LabWork, загруженная из файла
     * @throws IOException если произошла ошибка чтения файла
     * @throws DeserializationException если произошла ошибка при десериализации данных
     */
    public Collection<LabWork> loadCollection() throws IOException, DeserializationException {
        List<LabWork> collection = new LinkedList<>();
        File file = new File(filepath);
        if (!file.exists()) {
            file.createNewFile();
            return collection;
        }
        if (file.length() == 0) {
            return collection;
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreEmptyLines()
                     .withTrim()
                     .parse(reader)) {
            for (CSVRecord record : parser) {
                try {
                    LabWork labWork = parseLabWork(record);
                    if (labWork.validate()) {
                        collection.add(labWork);
                    } else {
                        throw new DeserializationException("Некорректные данные в записи (ID: " + record.get("id") + ")");
                    }
                } catch (Exception e) {
                    throw new DeserializationException(
                            "Ошибка парсинга записи (строка " + record.getRecordNumber() + "): " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new IOException("Ошибка чтения файла: " + e.getMessage(), e);
        }

        // Возвращаем отсортированный список
        return collection.stream()
                .sorted() // Сортируем по реализации Comparable в LabWork
                .collect(Collectors.toList());
    }

    /**
     * Метод для создания объекта LabWork из записи CSV.
     *
     * @param record запись CSV, содержащая данные для создания объекта LabWork
     * @return объект LabWork
     * @throws DeserializationException если произошла ошибка при создании объекта
     */
    private LabWork parseLabWork(CSVRecord record) throws DeserializationException {
        try {
            int id = parseIntField(record, "id");
            String name = parseStringField(record, "name");
            Coordinates coordinates = parseCoordinates(record);
            ZonedDateTime creationDate = parseDateField(record, "creationDate");
            float minimalPoint = parseFloatField(record, "minimalPoint");
            int tunedInWorks = parseIntField(record, "tunedInWorks");
            Difficulty difficulty = parseDifficulty(record);
            Discipline discipline = parseDiscipline(record);

            return new LabWork(id, name, coordinates, creationDate,
                    minimalPoint, tunedInWorks, difficulty, discipline);
        } catch (Exception e) {
            throw new DeserializationException("Ошибка создания LabWork: " + e.getMessage());
        }
    }

    /**
     * Метод для создания объекта Coordinates из записи CSV.
     *
     * @param record запись CSV, содержащая данные для создания объекта Coordinates
     * @return объект Coordinates
     * @throws DeserializationException если произошла ошибка при создании объекта
     */
    private Coordinates parseCoordinates(CSVRecord record) throws DeserializationException {
        try {
            long x = parseLongField(record, "x");
            int y = parseIntField(record, "y");
            return new Coordinates(x, y);
        } catch (Exception e) {
            throw new DeserializationException("Ошибка парсинга Coordinates: " + e.getMessage());
        }
    }

    /**
     * Метод для создания объекта Discipline из записи CSV.
     *
     * @param record запись CSV, содержащая данные для создания объекта Discipline
     * @return объект Discipline или null, если данные отсутствуют
     * @throws DeserializationException если произошла ошибка при создании объекта
     */
    private Discipline parseDiscipline(CSVRecord record) throws DeserializationException {
        String name = record.get("disciplineName");
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        try {
            return new Discipline(
                    name.trim(),
                    parseOptionalLong(record.get("disciplineLectureHours")),
                    parseOptionalLong(record.get("disciplinePracticeHours")),
                    parseLongField(record, "disciplineSelfStudyHours"),
                    parseIntField(record, "disciplineLabsCount")
            );
        } catch (Exception e) {
            throw new DeserializationException("Ошибка парсинга Discipline: " + e.getMessage());
        }
    }

    // Вспомогательные методы для парсинга полей

    /**
     * Метод для получения строкового значения поля из записи CSV.
     *
     * @param record запись CSV
     * @param field имя поля
     * @return значение поля
     * @throws DeserializationException если поле пустое или отсутствует
     */
    private String parseStringField(CSVRecord record, String field) throws DeserializationException {
        String value = record.get(field);
        if (value == null || value.trim().isEmpty()) {
            throw new DeserializationException("Поле " + field + " не может быть пустым");
        }
        return value.trim();
    }

    /**
     * Метод для получения целочисленного значения поля из записи CSV.
     *
     * @param record запись CSV
     * @param field имя поля
     * @return значение поля
     * @throws DeserializationException если значение некорректно
     */
    private int parseIntField(CSVRecord record, String field) throws DeserializationException {
        try {
            return Integer.parseInt(record.get(field).trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DeserializationException("Некорректное значение поля " + field);
        }
    }

    /**
     * Метод для получения длинного целочисленного значения поля из записи CSV.
     *
     * @param record запись CSV
     * @param field имя поля
     * @return значение поля
     * @throws DeserializationException если значение некорректно
     */
    private long parseLongField(CSVRecord record, String field) throws DeserializationException {
        try {
            return Long.parseLong(record.get(field).trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DeserializationException("Некорректное значение поля " + field);
        }
    }

    /**
     * Метод для получения значения с плавающей точкой из записи CSV.
     *
     * @param record запись CSV
     * @param field имя поля
     * @return значение поля
     * @throws DeserializationException если значение некорректно
     */
    private float parseFloatField(CSVRecord record, String field) throws DeserializationException {
        try {
            return Float.parseFloat(record.get(field).trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DeserializationException("Некорректное значение поля " + field);
        }
    }

    /**
     * Метод для получения значения даты и времени из записи CSV.
     *
     * @param record запись CSV
     * @param field имя поля
     * @return значение поля в формате ZonedDateTime
     * @throws DeserializationException если значение некорректно
     */
    private ZonedDateTime parseDateField(CSVRecord record, String field) throws DeserializationException {
        try {
            return ZonedDateTime.parse(record.get(field).trim(), DATE_FORMATTER);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new DeserializationException("Некорректный формат даты в поле " + field);
        }
    }

    /**
     * Метод для получения значения перечисления Difficulty из записи CSV.
     *
     * @param record запись CSV
     * @return значение перечисления Difficulty
     * @throws DeserializationException если значение некорректно
     */
    private Difficulty parseDifficulty(CSVRecord record) throws DeserializationException {
        try {
            return Difficulty.valueOf(record.get("difficulty").trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new DeserializationException("Некорректное значение Difficulty");
        }
    }

    /**
     * Метод для получения опционального значения типа Long из строки.
     *
     * @param value строковое значение
     * @return значение типа Long или null, если значение пустое или некорректно
     */
    private Long parseOptionalLong(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Метод для форматирования даты и времени в строку.
     *
     * @param date значение типа ZonedDateTime
     * @return отформатированная строка
     */
    private String formatDate(ZonedDateTime date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Метод для получения значения поля из объекта Discipline.
     *
     * @param d объект Discipline
     * @param getter функция для получения значения поля
     * @param <T> тип возвращаемого значения
     * @return значение поля или null, если объект Discipline равен null
     */
    private <T> T getDisciplineField(Discipline d, java.util.function.Function<Discipline, T> getter) {
        return d != null ? getter.apply(d) : null;
    }
}