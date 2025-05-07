package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * Head - команда для вывода первого элемента коллекции.
 */
public class Head extends UserCommand {
    public Head(IOManager ioManager, NetworkClient networkClient) {
        super("head", "head : вывести первый элемент коллекции", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'head' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), List.of());
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при получении данных: " + e.getMessage());
        }
    }
}