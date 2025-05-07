package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * Exit - команда для завершения работы клиента.
 */
public class Exit extends UserCommand {
    public Exit(IOManager ioManager, NetworkClient networkClient) {
        super("exit", "exit : завершить работу клиента", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'exit' не принимает аргументов");
        }

        ioManager.writeLine("Завершение работы клиента...\n");
        System.exit(0);
        return new ServerResponse(ServerResponseType.SUCCESS, "Работа клиента завершена");
    }
}