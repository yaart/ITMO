package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * RemoveFirst - клиентская команда для удаления первого элемента коллекции.
 */
public class RemoveFirst extends UserCommand {
    public RemoveFirst(IOManager ioManager, NetworkClient networkClient) {
        super("remove_first", "remove_first : удалить первый элемент из коллекции", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'remove_first' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), List.of());
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при удалении первого элемента: " + e.getMessage());
        }
    }
}