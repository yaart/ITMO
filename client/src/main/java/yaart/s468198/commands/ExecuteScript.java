package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.exceptions.ScriptException;
import yaart.s468198.network.NetworkClient;
import yaart.s468198.script.ScriptManager;
import yaart.s468198.manager.ClientCommandManager;

import java.io.Serializable;
import java.util.List;

/**
 * ExecuteScript - клиентская команда для запуска скрипта.
 */
public class ExecuteScript extends UserCommand {
    private final ClientCommandManager commandManager;
    private final IOManager ioManager;

    public ExecuteScript(IOManager ioManager, NetworkClient networkClient, ClientCommandManager commandManager) {
        super("execute_script", "execute_script file_name : считать и исполнить скрипт из указанного файла", ioManager, networkClient);
        this.commandManager = commandManager;
        this.ioManager = ioManager;
    }

    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (args.size() != 1 || !(args.get(0) instanceof String filename)) {
            throw new CommandArgumentException("Команда 'execute_script' ожидает имя файла");
        }

        try {
            ScriptManager scriptManager = new ScriptManager(filename, commandManager, ioManager);
            scriptManager.runScript();
            return new ServerResponse(ServerResponseType.SUCCESS, "Скрипт '" + filename + "' успешно выполнен");
        } catch (ScriptException e) {
            return new ServerResponse(ServerResponseType.ERROR, "Ошибка при выполнении скрипта: " + e.getMessage());
        } catch (Exception e) {
            return new ServerResponse(ServerResponseType.CORRUPTED, "Неожиданная ошибка: " + e.getMessage());
        }
    }
}