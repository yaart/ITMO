package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * Clear - команда для очистки коллекции на сервере.
 */
public class Clear extends UserCommand {
    public Clear(IOManager ioManager, NetworkClient networkClient) {
        super("clear", "clear : очистить коллекцию", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'clear' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), List.of());
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при очистке: " + e.getMessage());
        }
    }
}