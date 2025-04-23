package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.Discipline;

import java.util.List;

/**
 * Команда для вывода дисциплин в порядке возрастания.
 *
 * Класс реализует команду "print_field_ascending_discipline", которая позволяет пользователю
 * вывести значения поля discipline всех элементов коллекции в порядке возрастания.
 */
public class PrintFieldAscendingDiscipline extends UserCommand {
    /**
     * Менеджер коллекции, используемый для получения и сортировки дисциплин.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для получения дисциплин в порядке возрастания
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public PrintFieldAscendingDiscipline(CollectionManager collectionManager, IOManager ioManager) {
        super("print_field_ascending_discipline",
                "вывести значения поля discipline всех элементов в порядке возрастания",
                ioManager);
        this.collectionManager = collectionManager;
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

        // Получение списка дисциплин в порядке возрастания
        List<Discipline> disciplines = collectionManager.getDisciplinesAscending();

        // Вывод результата пользователю
        if (disciplines.isEmpty()) {
            ioManager.writeLine("Нет элементов с дисциплинами");
        } else {
            ioManager.writeLine("Дисциплины в порядке возрастания:");
            disciplines.forEach(d -> ioManager.writeLine(" " + d.getName()));
        }
    }
}