package yaart.s468198.commands;

import yaart.s468198.base.exceptions.ElementNotFoundException;
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
 * UpdateId - команда для обновления элемента по его ID.
 */
public class UpdateId extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды
     *
     * @param collectionManager менеджер коллекции
     */
    public UpdateId(CollectionManager collectionManager) {
        super("update", "update id {element} : обновить значение элемента коллекции, id которого равен заданному");
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
        if (args.size() != 1 || !(args.get(0) instanceof LabWork newLabWork)) {
            throw new CommandArgumentException("Команда 'update' ожидает один аргумент типа LabWork");
        }

        int id = newLabWork.getId();

        try {
            collectionManager.updateLabWork(newLabWork);
            logger.info("Команда '{}': элемент с ID {} успешно обновлён", getName(), id);
            return new ServerResponse(ServerResponseType.SUCCESS, "Элемент с ID " + id + " успешно обновлён.");
        } catch (ElementNotFoundException e) {
            logger.warn("Команда '{}': элемент с ID {} не найден", getName(), id);
            return new ServerResponse(ServerResponseType.NOT_FOUND, "Элемент с ID " + id + " не найден.");
        } catch (IllegalArgumentException e) {
            logger.error("Команда '{}': {}", getName(), e.getMessage());
            return new ServerResponse(ServerResponseType.CORRUPTED, "Ошибка данных: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Команда '{}': ошибка при обновлении: {}", getName(), e.getMessage());
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при обновлении: " + e.getMessage());
        }
    }
}