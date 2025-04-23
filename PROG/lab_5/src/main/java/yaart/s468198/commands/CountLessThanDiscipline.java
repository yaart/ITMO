package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.fieldReader.DisciplineFieldReader;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.Discipline;

import java.util.List;

/**
 * Команда для подсчета элементов с дисциплиной меньше заданной.
 *
 * Класс реализует команду "count_less_than_discipline", которая позволяет пользователю
 * подсчитать количество элементов в коллекции, значение поля discipline которых меньше заданного.
 */
public class CountLessThanDiscipline extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Объект для чтения данных о дисциплине от пользователя.
     */
    private final DisciplineFieldReader disciplineReader;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для выполнения операций над элементами
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public CountLessThanDiscipline(CollectionManager collectionManager, IOManager ioManager) {
        super("count_less_than_discipline",
                "count_less_than_discipline discipline: вывести количество элементов, значение поля discipline которых меньше заданного",
                ioManager);
        this.collectionManager = collectionManager;
        this.disciplineReader = new DisciplineFieldReader(ioManager);
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args аргументы команды (не должны быть указаны для этой команды)
     * @throws CommandArgumentException если переданы аргументы, которые не поддерживаются командой
     */
    @Override
    public void execute(List<String> args) {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Эта команда не принимает аргументы");
        }

        try {
            // Чтение данных о дисциплине от пользователя
            Discipline discipline = disciplineReader.executeDiscipline();

            // Подсчет количества элементов с меньшей дисциплиной
            long count = collectionManager.countLessThanDiscipline(discipline);

            // Вывод результата пользователю
            ioManager.writeLine("Количество элементов с меньшей дисциплиной: " + count);
        } catch (Exception e) {
            // Обработка ошибок при выполнении команды
            ioManager.writeError("Ошибка при подсчете элементов: " + e.getMessage());
        }
    }
}