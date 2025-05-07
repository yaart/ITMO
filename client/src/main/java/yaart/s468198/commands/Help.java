package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * Help - команда для получения справки по командам от сервера.
 */
public class Help extends UserCommand {
    public Help(IOManager ioManager, NetworkClient networkClient) {
        super("help", "help : вывести справку по доступным командам", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'help' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), args);
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при получении справки: " + e.getMessage());
        }
    }
}