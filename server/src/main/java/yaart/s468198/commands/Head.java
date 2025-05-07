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
 * Head - команда для получения первого элемента коллекции.
 */
public class Head extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    public Head(CollectionManager collectionManager) {
        super("head", "head : вывести первый элемент коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'head' не принимает аргументов");
        }

        LabWork head = collectionManager.getHead();

        if (head == null) {
            logger.info("Команда '{}' : коллекция пустая", getName());
            return new ServerResponse(ServerResponseType.EMPTY_COLLECTION, "Коллекция пуста");
        }

        logger.info("Команда '{}': найден элемент ID {}", getName(), head.getId());
        return new ServerResponse(ServerResponseType.SUCCESS, head.toString());
    }
}