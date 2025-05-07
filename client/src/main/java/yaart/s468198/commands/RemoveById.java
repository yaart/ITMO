package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * RemoveById - клиентская команда для удаления элемента по ID.
 */
public class RemoveById extends UserCommand {
    public RemoveById(IOManager ioManager, NetworkClient networkClient) {
        super("remove_by_id", "remove_by_id id : удалить элемент из коллекции по его ID", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (args.isEmpty() || !(args.get(0) instanceof String)) {
            throw new CommandArgumentException("Команда 'remove_by_id' требует один аргумент — ID");
        }

        try {
            int id = Integer.parseInt((String) args.get(0));
            return sendAndReceive(getName(), List.of(id));
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("ID должен быть числом");
        } catch (IOException | ClassNotFoundException e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка сети: " + e.getMessage());
        }
    }
}