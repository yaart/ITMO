package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.Serializable;
import java.util.List;

/**
 * FilterBySize - клиентская команда для фильтрации элементов по длине вектора координат.
 */
public class FilterBySize extends UserCommand {
    public FilterBySize(IOManager ioManager, NetworkClient networkClient) {
        super("filter_by_size", "filter_by_size {size}: вывести элементы, размер которых меньше заданного", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (args.isEmpty()) {
            throw new CommandArgumentException("Команда 'filter_by_size' требует аргумент — размер (int)");
        }

        try {
            int size = Integer.parseInt((String) args.get(0));
            return sendAndReceive(getName(), List.of(size));
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("Аргумент должен быть целым числом");
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при фильтрации: " + e.getMessage());
        }
    }
}