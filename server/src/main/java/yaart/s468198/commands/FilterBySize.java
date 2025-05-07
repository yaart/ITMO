package yaart.s468198.commands;

import yaart.s468198.base.models.Coordinates;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponseWithLabWorkList;
import yaart.s468198.managers.CollectionManager;
import java.util.List;
import java.io.Serializable;
import java.util.stream.Collectors;

import static java.lang.Math.pow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FilterBySize - команда для фильтрации элементов по размеру.
 */
public class FilterBySize extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public FilterBySize(CollectionManager collectionManager) {
        super("filter_by_size", "filter_by_size {size}: вывести элементы, размер которых меньше заданного");
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
            throw new CommandArgumentException("Команда 'filter_by_size' должна принимать один аргумент — размер.");
        }

        Object arg = args.get(0);
        if (!(arg instanceof Integer || arg instanceof Long)) {
            throw new CommandArgumentException("Размер должен быть целым числом.");
        }

        int size;
        try {
            size = ((Number) arg).intValue();
        } catch (Exception e) {
            throw new CommandArgumentException("Неверный формат размера.");
        }

        List<LabWork> filtered = collectionManager.getCollection().stream()
                .filter(lw -> {
                    Coordinates c = lw.getCoordinates();
                    return c != null && pow(c.getX(), 2) + pow(c.getY(), 2) <= size;
                })
                .collect(Collectors.toList());

        String message = "Найдено " + filtered.size() + " элементов с размером <= " + size;
        logger.info("Команда '{}': {}", getName(), message);

        return new ServerResponseWithLabWorkList(
                ServerResponseType.SUCCESS,
                message,
                filtered
        );
    }
}