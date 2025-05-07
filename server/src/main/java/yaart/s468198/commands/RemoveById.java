package yaart.s468198.commands;

import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;

import java.util.List;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RemoveById - команда для удаления элемента коллекции по его ID.
 */
public class RemoveById extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id", "remove_by_id id : удалить элемент из коллекции по его ID");
        this.collectionManager = collectionManager;
    }

    /**
     * Метод для выполнения команды
     *
     * @param args список аргументов
     * @return результат выполнения команды
     * @throws CommandArgumentException если количество или тип аргументов неверный
     */
    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (args.size() != 1) {
            throw new CommandArgumentException("Команда 'remove_by_id' должна принимать один аргумент — ID.");
        }

        Integer id;
        try {
            id = (Integer) args.get(0);
        } catch (ClassCastException | NullPointerException e) {
            throw new CommandArgumentException("ID должен быть целым числом.");
        }

        LabWork labWork = collectionManager.getLabWorkById(id);

        if (labWork == null) {
            logger.warn("Команда '{}': элемент с ID {} не найден", getName(), id);
            return new ServerResponse(ServerResponseType.NOT_FOUND, "Элемент с ID " + id + " не найден.");
        }

        try {
            collectionManager.removeLabWorkById(id);
            logger.info("Команда '{}': элемент с ID {} удален", getName(), id);
            return new ServerResponse(ServerResponseType.SUCCESS, "Элемент с ID " + id + " успешно удален.");
        } catch (Exception e) {
            logger.error("Команда '{}': ошибка при удалении: {}", getName(), e.getMessage());
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при удалении: " + e.getMessage());
        }
    }
}