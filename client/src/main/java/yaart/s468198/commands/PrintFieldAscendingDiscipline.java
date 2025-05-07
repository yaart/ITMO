package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * PrintFieldAscendingDiscipline - клиентская команда для вывода дисциплин по возрастанию.
 */
public class PrintFieldAscendingDiscipline extends UserCommand {
    public PrintFieldAscendingDiscipline(IOManager ioManager, NetworkClient networkClient) {
        super("print_field_ascending_discipline", "print_field_ascending_discipline : вывести все значения поля discipline в порядке возрастания", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'print_field_ascending_discipline' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), args);
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при получении данных: " + e.getMessage());
        }
    }
}