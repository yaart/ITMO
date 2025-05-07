package yaart.s468198.commands;

import yaart.s468198.base.models.Discipline;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import java.util.List;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * CountLessThanDiscipline - команда для подсчёта количества элементов,
 * у которых дисциплина меньше заданной.
 */
public class CountLessThanDiscipline extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public CountLessThanDiscipline(CollectionManager collectionManager) {
        super("count_less_than_discipline", "count_less_than_discipline {discipline}: подсчёт количества элементов, у которых дисциплина меньше заданной");
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
            throw new CommandArgumentException("Команда 'count_less_than_discipline' должна принимать один аргумент — объект Discipline.");
        }

        Object obj = args.get(0);
        if (!(obj instanceof Discipline discipline)) {
            throw new CommandArgumentException("Первый аргумент должен быть объектом типа Discipline.");
        }

        long count = collectionManager.getCollection().stream()
                .filter(lw -> lw.getDiscipline() != null && lw.getDiscipline().compareTo(discipline) < 0)
                .count();

        String message = "Найдено " + count + " элементов с дисциплиной меньше заданной.";
        logger.info("Команда '{}': {}", getName(), message);

        return new ServerResponse(ServerResponseType.SUCCESS, message);
    }
}