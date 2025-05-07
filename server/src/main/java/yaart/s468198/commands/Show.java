package yaart.s468198.commands;

import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponseWithLabWorkList;
import yaart.s468198.managers.CollectionManager;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Show - команда для вывода всех элементов коллекции.
 */
public class Show extends UserCommand {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "show : вывести все элементы коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'show' не принимает аргументов");
        }

        List<LabWork> labWorks = collectionManager.getCollection().stream()
                .distinct()
                .sorted()
                .toList();

        if (labWorks.isEmpty()) {
            return new ServerResponse(ServerResponseType.EMPTY_COLLECTION, "Коллекция пуста");
        }

        StringBuilder result = new StringBuilder("Элементы коллекции:\n");
        for (LabWork lw : labWorks) {
            result.append(lw).append("\n");
        }

        return new ServerResponse(ServerResponseType.SUCCESS, result.toString());
    }
}