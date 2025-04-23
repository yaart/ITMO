package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.fieldReader.LabWorkFieldReader;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.LabWork;

import java.util.List;

/**
 * Команда для удаления элементов, меньших чем заданный.
 *
 * Класс реализует команду "remove_lower", которая позволяет пользователю
 * удалить из коллекции все элементы, которые меньше заданного элемента.
 */
public class RemoveLower extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Объект для чтения данных LabWork от пользователя.
     */
    private final LabWorkFieldReader labWorkReader;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для удаления элементов
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public RemoveLower(CollectionManager collectionManager, IOManager ioManager) {
        super("remove_lower", "remove_lower {element}: удалить из коллекции все элементы, меньшие, чем заданный", ioManager);
        this.collectionManager = collectionManager;
        this.labWorkReader = new LabWorkFieldReader(ioManager);
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
            // Создание временного элемента для сравнения (ID не используется)
            int dummyId = -1; // Временный ID, так как элемент не будет добавлен в коллекцию
            LabWork element = labWorkReader.executeLabWork(dummyId);

            // Удаление элементов, меньших заданного
            int beforeSize = collectionManager.size();
            collectionManager.removeLower(element);
            int removedCount = beforeSize - collectionManager.size();

            // Вывод количества удаленных элементов
            ioManager.writeLine("Удалено элементов: " + removedCount);
        } catch (Exception e) {
            // Обработка ошибок при выполнении команды
            ioManager.writeError("Ошибка при удалении элементов: " + e.getMessage());
        }
    }
}