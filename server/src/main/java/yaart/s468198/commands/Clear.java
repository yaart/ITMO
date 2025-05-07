package yaart.s468198.commands;

import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import java.util.List;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Clear - команда для очистки коллекции LabWork.
 */
public class Clear extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public Clear(CollectionManager collectionManager) {
        super("clear", "clear: очистить коллекцию");
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
            throw new CommandArgumentException("Команда 'clear' не принимает аргументов.");
        }

        try {
            collectionManager.clear();
            logger.info("Коллекция успешно очищена пользователем");
            return new ServerResponse(ServerResponseType.SUCCESS, "Коллекция успешно очищена.");
        } catch (Exception e) {
            logger.error("Ошибка при очистке коллекции: {}", e.getMessage());
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при очистке коллекции: " + e.getMessage());
        }
    }
}