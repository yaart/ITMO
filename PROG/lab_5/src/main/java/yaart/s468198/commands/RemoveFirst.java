package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.LabWork;

import java.util.List;

/**
 * Команда для удаления первого элемента коллекции.
 *
 * Класс реализует команду "remove_first", которая позволяет пользователю
 * удалить первый элемент из коллекции. Если коллекция пуста, выводится соответствующее сообщение.
 */
public class RemoveFirst extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для удаления первого элемента
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public RemoveFirst(CollectionManager collectionManager, IOManager ioManager) {
        super("remove_first", "удалить первый элемент из коллекции", ioManager);
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

        // Проверка, пуста ли коллекция
        if (collectionManager.isEmpty()) {
            ioManager.writeLine("Коллекция пуста, нечего удалять");
            return;
        }

        // Удаление первого элемента
        LabWork removed = collectionManager.removeFirst();

        // Вывод информации об удаленном элементе
        ioManager.writeLine("Удален первый элемент:");
        ioManager.writeLine(removed.toString());
    }
}