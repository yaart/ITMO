package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.managers.CollectionManager;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Команда для добавления новой лабораторной работы в коллекцию.
 */
public class Add extends UserCommand {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        super("add", "add {element}: добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (args.size() != 1 || !(args.get(0) instanceof LabWork labWork)) {
            throw new CommandArgumentException("Команда 'add' ожидает один аргумент типа LabWork");
        }

        try {
            int id = collectionManager.generateId();

            LabWork updatedLabWork = new LabWork(
                    id,
                    labWork.getName(),
                    labWork.getCoordinates(),
                    ZonedDateTime.now(),
                    labWork.getMinimalPoint(),
                    labWork.getTunedInWorks(),
                    labWork.getDifficulty(),
                    labWork.getDiscipline()
            );

            if (!updatedLabWork.validate()) {
                return new ServerResponse(ServerResponseType.CORRUPTED, "Объект содержит некорректные данные");
            }

            collectionManager.addLabWork(updatedLabWork);
            return new ServerResponse(ServerResponseType.SUCCESS, "Лабораторная работа успешно добавлена");

        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при добавлении: " + e.getMessage());
        }
    }
}