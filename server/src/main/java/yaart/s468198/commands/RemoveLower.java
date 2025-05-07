package yaart.s468198.commands;

import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;

import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * RemoveLower - команда для удаления всех элементов с ID меньше заданного.
 */
public class RemoveLower extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "remove_lower id : удалить все элементы с ID меньше указанного");
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
            throw new CommandArgumentException("Команда 'remove_lower' должна принимать один аргумент — ID.");
        }

        Integer keyId;
        try {
            keyId = (Integer) args.get(0);
        } catch (ClassCastException | NullPointerException e) {
            throw new CommandArgumentException("ID должен быть целым числом.");
        }

        // Получаем список ID до удаления
        List<Integer> removedIds = collectionManager.getCollection().stream()
                .filter(lw -> lw.getId() < keyId)
                .map(LabWork::getId)
                .toList();

        if (removedIds.isEmpty()) {
            logger.info("Команда '{}': нет элементов с ID < {}", getName(), keyId);
            return new ServerResponse(ServerResponseType.EMPTY_COLLECTION,
                    "Нет элементов с ID меньше " + keyId + ".");
        }

        try {
            collectionManager.removeLower(keyId); // удаляем элементы на самом деле
        } catch (Exception e) {
            logger.error("Ошибка при удалении элементов с ID < {}: {}", keyId, e.getMessage());
            return new ServerResponse(ServerResponseType.ERROR,
                    "Ошибка при удалении: " + e.getMessage());
        }

        String message = "Удалены элементы с ID: " + removedIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        logger.info("Команда '{}': {}", getName(), message);

        return new ServerResponse(ServerResponseType.SUCCESS, message);
    }
}