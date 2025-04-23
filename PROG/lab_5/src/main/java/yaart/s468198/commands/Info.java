package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;

/**
 * Команда для вывода информации о коллекции.
 *
 * Класс реализует команду "info", которая позволяет пользователю получить информацию о коллекции,
 * включая её тип, дату инициализации и количество элементов.
 */
public class Info extends UserCommand {
    /**
     * Менеджер коллекции, используемый для получения информации о коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для получения информации о коллекции
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Info(CollectionManager collectionManager, IOManager ioManager) {
        super("info", "вывести информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)", ioManager);
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

        // Вывод информации о коллекции
        ioManager.writeLine("Информация о коллекции:");
        ioManager.writeLine(" Тип: " + collectionManager.getCollection().getClass().getName());
        ioManager.writeLine(" Дата инициализации: " + collectionManager.getCollectionCreationDate());
        ioManager.writeLine(" Количество элементов: " + collectionManager.size());
    }
}