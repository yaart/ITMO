package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.exceptions.ElementNotFoundException;
import yaart.s468198.fieldReader.LabWorkFieldReader;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.LabWork;

import java.util.List;

/**
 * Команда для обновления элемента коллекции по ID.
 *
 * Класс реализует команду "update", которая позволяет пользователю обновить значение элемента коллекции
 * по его уникальному идентификатору (ID). Пользователь вводит новые данные для элемента,
 * которые заменяют текущие значения.
 */
public class UpdateId extends UserCommand {
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
     * @param collectionManager менеджер коллекции для обновления элементов
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public UpdateId(CollectionManager collectionManager, IOManager ioManager) {
        super("update", "update id {element}: обновить значение элемента коллекции по ID", ioManager);
        this.collectionManager = collectionManager;
        this.labWorkReader = new LabWorkFieldReader(ioManager);
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

            // Получение существующего элемента по ID
            LabWork existing = collectionManager.getLabWorkById(id);

            if (existing == null) {
                throw new ElementNotFoundException("Элемент с ID " + id + " не найден");
            }

            // Вывод текущих данных элемента
            ioManager.writeLine("Текущие данные элемента:");
            ioManager.writeLine(existing.toString());

            // Чтение новых данных от пользователя
            ioManager.writeLine("Введите новые данные:");
            LabWork updated = labWorkReader.executeLabWork(id);

            // Обновление элемента в коллекции
            collectionManager.updateLabWork(updated);

            // Уведомление пользователя об успешном обновлении
            ioManager.writeLine("Элемент с ID " + id + " успешно обновлен");
        } catch (NumberFormatException e) {
            // Обработка ошибки преобразования ID в число
            throw new CommandArgumentException("ID должен быть целым числом");
        } catch (Exception e) {
            // Обработка других ошибок при выполнении команды
            ioManager.writeError("Ошибка при обновлении элемента: " + e.getMessage());
        }
    }
}