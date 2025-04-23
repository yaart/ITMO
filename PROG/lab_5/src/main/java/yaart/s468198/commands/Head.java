package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.LabWork;

import java.util.List;

/**
 * Команда для вывода первого элемента коллекции.
 *
 * Класс реализует команду "head", которая позволяет пользователю получить первый элемент коллекции.
 * Если коллекция пуста, выводится соответствующее сообщение.
 */
public class Head extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для получения первого элемента
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Head(CollectionManager collectionManager, IOManager ioManager) {
        super("head", "вывести первый элемент коллекции", ioManager);
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

        // Получение первого элемента коллекции
        LabWork first = collectionManager.getHead();

        // Вывод результата пользователю
        if (first == null) {
            ioManager.writeLine("Коллекция пуста");
        } else {
            ioManager.writeLine("Первый элемент коллекции:");
            ioManager.writeLine(first.toString());
        }
    }
}