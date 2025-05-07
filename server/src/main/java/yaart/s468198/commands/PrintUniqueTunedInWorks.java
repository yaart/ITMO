package yaart.s468198.commands;

import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;

import java.util.List;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * PrintUniqueTunedInWorks - команда для вывода уникальных значений поля tunedInWorks.
 */
public class PrintUniqueTunedInWorks extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public PrintUniqueTunedInWorks(CollectionManager collectionManager) {
        super("print_unique_tuned_in_works", "print_unique_tuned_in_works : вывести уникальные значения поля tunedInWorks");
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
            throw new CommandArgumentException("Команда 'print_unique_tuned_in_works' не принимает аргументов.");
        }

        Set<Integer> uniqueValues = collectionManager.getCollection().stream()
                .map(LabWork::getTunedInWorks)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<String> sortedValues = uniqueValues.stream()
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.toList());

        if (sortedValues.isEmpty()) {
            logger.info("Команда '{}': нет элементов с полем tunedInWorks", getName());
            return new ServerResponse(ServerResponseType.EMPTY_COLLECTION, "Нет элементов с полем tunedInWorks.");
        }

        String responseText = String.join("\n", sortedValues);
        logger.info("Команда '{}': найдено {} уникальных значений", getName(), sortedValues.size());

        return new ServerResponse(ServerResponseType.SUCCESS, responseText);
    }
}