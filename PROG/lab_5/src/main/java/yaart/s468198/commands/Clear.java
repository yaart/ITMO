package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;

/**
 * Команда для очистки коллекции.
 *
 * Класс реализует команду "clear", которая позволяет пользователю очистить всю коллекцию элементов.
 */
public class Clear extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции, который будет очищен
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Clear(CollectionManager collectionManager, IOManager ioManager) {
        super("clear", "очистить коллекцию", ioManager);
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

        // Очистка коллекции
        collectionManager.clear();

        // Уведомление пользователя об успешной очистке
        ioManager.writeLine("Коллекция успешно очищена");
    }
}