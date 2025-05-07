package yaart.s468198.commands;

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
 * PrintFieldAscendingDiscipline - команда для вывода всех дисциплин из коллекции в порядке возрастания.
 */
public class PrintFieldAscendingDiscipline extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public PrintFieldAscendingDiscipline(CollectionManager collectionManager) {
        super("print_field_ascending_discipline", "print_field_ascending_discipline : вывести все дисциплины из коллекции в порядке возрастания");
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
            throw new CommandArgumentException("Команда 'print_field_ascending_discipline' не принимает аргументов.");
        }

        List<String> disciplines = collectionManager.getCollection().stream()
                .filter(lw -> lw.getDiscipline() != null)
                .map(lw -> lw.getDiscipline().getName())
                .sorted()
                .distinct()
                .collect(Collectors.toList());

        if (disciplines.isEmpty()) {
            return new ServerResponse(
                    ServerResponseType.EMPTY_COLLECTION,
                    "В коллекции нет элементов с дисциплиной."
            );
        }

        String responseText = String.join("\n", disciplines);
        logger.info("Команда '{}': найдено {} уникальных дисциплин", getName(), disciplines.size());

        return new ServerResponse(ServerResponseType.SUCCESS, responseText);
    }
}