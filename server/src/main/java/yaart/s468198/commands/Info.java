package yaart.s468198.commands;

import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;

import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Info - команда для получения информации о коллекции.
 */
public class Info extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public Info(CollectionManager collectionManager) {
        super("info", "info : вывести информацию о коллекции");
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
            throw new CommandArgumentException("Команда 'info' не принимает аргументов.");
        }

        String type = collectionManager.getCollectionType();
        ZonedDateTime creationDate = collectionManager.getCreationDate();
        int size = collectionManager.size();

        String builder = "Тип коллекции: " + type + "\n" +
                "Дата инициализации: " + creationDate.toString() + "\n" +
                "Число элементов: " + size;

        logger.info("Команда '{}': {}", getName(), "Информация о коллекции получена");

        return new ServerResponse(
                ServerResponseType.SUCCESS,
                builder
        );
    }
}