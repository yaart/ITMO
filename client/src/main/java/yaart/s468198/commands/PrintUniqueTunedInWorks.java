package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * PrintUniqueTunedInWorks - клиентская команда для вывода уникальных значений поля tunedInWorks.
 */
public class PrintUniqueTunedInWorks extends UserCommand {
    public PrintUniqueTunedInWorks(IOManager ioManager, NetworkClient networkClient) {
        super("print_unique_tuned_in_works", "print_unique_tuned_in_works : вывести уникальные значения поля tunedInWorks", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'print_unique_tuned_in_works' не принимает аргументов");
        }

        try {
            return sendAndReceive(getName(), List.of());
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при получении данных: " + e.getMessage());
        }
    }
}