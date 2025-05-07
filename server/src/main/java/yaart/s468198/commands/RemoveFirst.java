package yaart.s468198.commands;

import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;

import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RemoveFirst - команда для удаления первого элемента из коллекции.
 */
public class RemoveFirst extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public RemoveFirst(CollectionManager collectionManager) {
        super("remove_first", "remove_first : удалить первый элемент из коллекции");
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
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'remove_first' не принимает аргументов.");
        }

        if (collectionManager.isEmpty()) {
            return new ServerResponse(ServerResponseType.EMPTY_COLLECTION, "Коллекция пуста.");
        }

        LabWork first = collectionManager.removeFirst();

        if (first == null) {
            logger.warn("Команда '{}': не удалось найти первый элемент", getName());
            return new ServerResponse(ServerResponseType.ERROR, "Не удалось найти первый элемент.");
        }

        logger.info("Команда '{}': удален элемент с ID {}", getName(), first.getId());

        return new ServerResponse(ServerResponseType.SUCCESS, "Верхний элемент успешно удален.");
    }
}