package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.fieldReader.LabWorkFieldReader;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * UpdateId - клиентская команда для обновления элемента по ID.
 */
public class UpdateId extends UserCommand {
    public UpdateId(IOManager ioManager, NetworkClient networkClient) {
        super("update", "update id {element} : обновить элемент по ID", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException, InterruptedException, IOException, ClassNotFoundException {
        if (args.isEmpty()) {
            ioManager.writeLine("Обновление элемента");
            ioManager.write("Введите ID: ");
            int id = Integer.parseInt(ioManager.readLine().trim());

            LabWork labWork = new LabWorkFieldReader(ioManager).executeLabWork(id);
            return sendAndReceive(getName(), List.of(labWork));
        }
        else if (!(args.get(0) instanceof String)) {
            throw new CommandArgumentException("Команда 'remove_by_id' требует один аргумент — ID");
        }

        try {
            int id = Integer.parseInt((String) args.get(0));
            LabWork labWork = new LabWorkFieldReader(ioManager).executeLabWork(id);
            return sendAndReceive(getName(), List.of(labWork));
        } catch (NumberFormatException e) {
            throw new CommandArgumentException("ID должен быть числом");
        } catch (IOException | ClassNotFoundException e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка сети: " + e.getMessage());
        }


    }
}