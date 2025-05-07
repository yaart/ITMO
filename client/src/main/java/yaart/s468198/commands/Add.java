package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.exceptions.InvalidArgumentsException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.network.NetworkClient;
import yaart.s468198.base.models.LabWork;
import yaart.s468198.base.fieldReader.LabWorkFieldReader;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * Add - клиентская команда для добавления элемента в коллекцию.
 */
public class Add extends UserCommand {
    public Add(IOManager ioManager, NetworkClient networkClient) {
        super("add", "add : добавить новый элемент в коллекцию", ioManager, networkClient);
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        try {
            // Создаем LabWork через FieldReader
            LabWork labWork = new LabWorkFieldReader(ioManager).executeLabWork(52); // ID = 0 → генерируется на сервере
            return sendAndReceive(getName(), List.of(labWork));
        } catch (InterruptedException e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ввод прерван");
        } catch (InvalidArgumentsException e) {
            return new ServerResponse(ServerResponseType.CORRUPTED, "Неверные данные: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}