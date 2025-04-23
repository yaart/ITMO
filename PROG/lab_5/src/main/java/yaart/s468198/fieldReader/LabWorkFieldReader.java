package yaart.s468198.fieldReader;

import yaart.s468198.exceptions.InvalidArgumentsException;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.*;

import java.time.ZonedDateTime;

/**
 * LabWorkFieldReader - класс для чтения объекта LabWork из пользовательского ввода
 */
public class LabWorkFieldReader {
    private final IOManager ioManager;
    private final StringFieldReader nameReader;
    private final CoordinatesFieldReader coordinatesReader;
    private final FloatFieldReader minimalPointReader;
    private final IntegerFieldReader tunedInWorksReader;
    private final DifficultyFieldReader difficultyReader;
    private final DisciplineFieldReader disciplineReader;

    public LabWorkFieldReader(IOManager ioManager) {
        this.ioManager = ioManager;
        this.nameReader = new StringFieldReader(ioManager, "Введите название лабораторной работы:");
        this.coordinatesReader = new CoordinatesFieldReader(ioManager);
        this.minimalPointReader = new FloatFieldReader(ioManager, "Введите минимальный балл:");
        this.tunedInWorksReader = new IntegerFieldReader(ioManager, "Введите количество настроенных работ:");
        this.difficultyReader = new DifficultyFieldReader(ioManager);
        this.disciplineReader = new DisciplineFieldReader(ioManager);
    }

    /**
     * Создает объект LabWork на основе пользовательского ввода
     * @param id уникальный идентификатор (генерируется автоматически)
     * @return объект LabWork
     * @throws InterruptedException если ввод был прерван
     */
    public LabWork executeLabWork(int id) throws InterruptedException {
        ioManager.writeLine("\n=== Ввод данных LabWork ===");

        while(true) {
            try {
                // Чтение обязательных полей
                String name = nameReader.executeString();
                Coordinates coordinates = coordinatesReader.executeCoordinates();
                Float minimalPoint = minimalPointReader.executeFloat();
                Integer tunedInWorks = tunedInWorksReader.executeInteger();
                Difficulty difficulty = difficultyReader.executeDifficulty();

                // Опциональное поле discipline
                Discipline discipline = null;
                if (askAddDiscipline()) {
                    discipline = disciplineReader.executeDiscipline();
                }

                // Создание объекта
                LabWork labWork = new LabWork(
                        id,
                        name,
                        coordinates,
                        ZonedDateTime.now(), // Автоматическая генерация даты
                        minimalPoint,
                        tunedInWorks,
                        difficulty,
                        discipline
                );

                ioManager.writeLine("LabWork успешно создан (ID: " + id + ")");
                return labWork;

            } catch (InvalidArgumentsException e) {
                ioManager.writeError("Ошибка: " + e.getMessage());
                ioManager.writeLine("Пожалуйста, повторите ввод\n");
            }
        }
    }

    /**
     * Запрашивает у пользователя необходимость добавления дисциплины
     */
    private boolean askAddDiscipline() throws InterruptedException {
        StringFieldReader choiceReader = new StringFieldReader(ioManager,
                "Добавить дисциплину? (y/n):");

        while(true) {
            String choice = choiceReader.executeString().toLowerCase();
            if(choice.equals("y") || choice.equals("yes")) {
                return true;
            } else if(choice.equals("n") || choice.equals("no")) {
                return false;
            }
            ioManager.writeError("Пожалуйста, введите 'y' или 'n'");
        }
    }
}