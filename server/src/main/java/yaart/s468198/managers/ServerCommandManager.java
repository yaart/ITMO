package yaart.s468198.managers;

import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.exceptions.UnknownCommandException;
import yaart.s468198.commands.UserCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.*;
import java.io.Serializable;



/**
 * ServerCommandManager - менеджер команд сервера.
 */
public class ServerCommandManager {
    private final Logger logger = LogManager.getRootLogger();
    private final Map<String, UserCommand> commands = new HashMap<>();

    /**
     * Конструктор класса.
     */
    public ServerCommandManager() {}

    /**
     * Метод для добавления новой команды.
     *
     * @param command команда, которую нужно зарегистрировать
     */
    public void addCommand(UserCommand command) {
        this.commands.put(command.getName(), command);
    }

    /**
     * Метод для исполнения команды.
     *
     * @param commandName имя команды
     * @param args аргументы команды
     * @return результат выполнения команды
     * @throws UnknownCommandException если команда не найдена
     */
    public ServerResponse execute(String commandName, List<Serializable> args) throws UnknownCommandException {
        UserCommand executingCommand = this.commands.get(commandName);

        if (executingCommand == null) {
            logger.warn("Неопознанная команда: " + commandName);
            throw new UnknownCommandException("Неопознанная команда");
        }

        try {
            logger.info("Вызов команды: " + commandName);
            return executingCommand.execute(args);
        } catch (CommandArgumentException e) {
            logger.error("Ошибка исполнения команды " + commandName + ": " + e.getMessage());
            return new ServerResponse(
                    ServerResponseType.INVALID_ARGUMENTS,
                    "Ошибка исполнения команды: " + e.getMessage()
            );
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при выполнении команды " + commandName + ": " + e.getMessage());
            return new ServerResponse(
                    ServerResponseType.ERROR,
                    "Неожиданная ошибка: " + e.getMessage()
            );
        }
    }

    /**
     * Возвращает список всех доступных команд.
     *
     * @return список команд
     */
    public Collection<UserCommand> getCommands() {
        return Collections.unmodifiableCollection(this.commands.values());
    }
}