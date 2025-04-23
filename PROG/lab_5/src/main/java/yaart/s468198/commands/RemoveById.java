package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.exceptions.ElementNotFoundException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;

/**
 * Команда для удаления элемента по ID.
 *
 * Класс реализует команду "remove_by_id", которая позволяет пользователю
 * удалить элемент из коллекции по его уникальному идентификатору (ID).
 */
public class RemoveById extends UserCommand {
    /**
     * Менеджер коллекции, используемый для управления элементами коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для удаления элементов
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public RemoveById(CollectionManager collectionManager, IOManager ioManager) {
        super("remove_by_id", "remove_by_id id: удалить элемент из коллекции по его id", ioManager);
        this.collectionManager = collectionManager;
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args аргументы команды (должен быть указан один аргумент - ID элемента)
     * @throws CommandArgumentException если количество аргументов неверно или ID не является целым числом
     */
    @Override
    public void execute(List<String> args) {
        if (args.size() != 1) {
            throw new CommandArgumentException("Требуется 1 аргумент - ID элемента");
        }

        try {
            // Преобразование аргумента в целое число
            int id = Integer.parseInt(args.get(0));

            // Удаление элемента по ID
            collectionManager.removeLabWorkById(id);

            // Уведомление пользователя об успешном удалении
            ioManager.writeLine("Элемент с ID " + id + " успешно удален");
        } catch (NumberFormatException e) {
            // Обработка ошибки преобразования ID в число
            throw new CommandArgumentException("ID должен быть целым числом");
        } catch (ElementNotFoundException e) {
            // Обработка случая, когда элемент с указанным ID не найден
            ioManager.writeError(e.getMessage());
        }
    }
}