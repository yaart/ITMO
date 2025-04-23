package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.parser.CsvCollectionManager;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;

/**
 * Команда для сохранения коллекции в файл.
 *
 * Класс реализует команду "save", которая позволяет пользователю сохранить текущее состояние коллекции
 * в файл в формате CSV. Для выполнения этой операции используется CsvCollectionManager.
 */
public class Save extends UserCommand {
    /**
     * Менеджер коллекции, используемый для получения элементов коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Менеджер для работы с CSV-файлами, используемый для сохранения коллекции.
     */
    private final CsvCollectionManager csvManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для получения элементов коллекции
     * @param csvManager менеджер для работы с CSV-файлами
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Save(CollectionManager collectionManager, CsvCollectionManager csvManager, IOManager ioManager) {
        super("save", "сохранить коллекцию в файл", ioManager);
        this.collectionManager = collectionManager;
        this.csvManager = csvManager;
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
            // Сохранение коллекции в файл
            csvManager.saveCollection(collectionManager.getCollection());

            // Уведомление пользователя об успешном сохранении
            ioManager.writeLine("Коллекция успешно сохранена");
        } catch (Exception e) {
            // Обработка ошибок при сохранении коллекции
            ioManager.writeError("Ошибка при сохранении коллекции: " + e.getMessage());
        }
    }
}