package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * Info - клиентская команда для получения информации о коллекции.
 */
public class Info extends UserCommand {
    public Info(IOManager ioManager, NetworkClient networkClient) {
        super("info", "info : вывести информацию о коллекции", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'info' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), List.of());
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при получении данных: " + e.getMessage());
        }
    }
}