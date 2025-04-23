package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.fieldReader.LabWorkFieldReader;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.LabWork;

import java.util.List;

/**
 * Команда для добавления нового элемента в коллекцию.
 *
 * Класс реализует команду "add", которая позволяет пользователю добавлять новый объект LabWork
 * в коллекцию. Для создания нового объекта используется LabWorkFieldReader.
 */
public class Add extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Объект для чтения полей LabWork от пользователя.
     */
    private final LabWorkFieldReader labWorkReader;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для добавления новых элементов
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Add(CollectionManager collectionManager, IOManager ioManager) {
        super("add", "добавить новый элемент в коллекцию", ioManager);
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
            // Генерация нового ID для элемента
            int newId = collectionManager.generateId();

            // Чтение данных для нового объекта LabWork
            LabWork labWork = labWorkReader.executeLabWork(newId);

            // Добавление нового объекта в коллекцию
            collectionManager.addLabWork(labWork);

            // Уведомление пользователя об успешном добавлении
            ioManager.writeLine("Элемент успешно добавлен с ID: " + newId);
        } catch (Exception e) {
            // Обработка ошибок при добавлении элемента
            ioManager.writeError("Ошибка при добавлении элемента: " + e.getMessage());
        }
    }
}