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
 * RemoveLower - клиентская команда для удаления всех элементов с ID меньше заданного.
 */
public class RemoveLower extends UserCommand {
    public RemoveLower(IOManager ioManager, NetworkClient networkClient) {
        super("remove_lower", "remove_lower id : удалить все элементы с ID меньше указанного", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (args.isEmpty() || !(args.get(0) instanceof String)) {
            throw new CommandArgumentException("Команда 'remove_lower' требует один аргумент — ID");
        }

        try {
            int id = Integer.parseInt((String) args.get(0));
            return sendAndReceive(getName(), List.of(id));

        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при удалении: " + e.getMessage());
        }

    }
}