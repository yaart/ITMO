package yaart.s468198.commands;

import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.managers.ServerCommandManager;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Help - команда для вывода справки по доступным командам.
 */
public class Help extends UserCommand {
    private static final Logger logger = LogManager.getRootLogger();
    private final ServerCommandManager commandManager;

    /**
     * Конструктор команды.
     *
     * @param commandManager менеджер команд сервера
     */
    public Help(ServerCommandManager commandManager) {
        super("help", "help : вывести справку по доступным командам");
        this.commandManager = commandManager;
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args список аргументов
     * @return результат выполнения команды
     * @throws CommandArgumentException если переданы лишние аргументы
     */
    @Override
    public ServerResponse execute(List<Serializable> args) throws CommandArgumentException {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Команда 'help' не принимает аргументов");
        }

        List<UserCommand> commands = new ArrayList<>(commandManager.getCommands());
        commands.sort(Comparator.comparing(UserCommand::getName));

        StringBuilder builder = new StringBuilder("Справка по командам:\n");
        for (UserCommand command : commands) {
            builder.append(String.format(" %-20s - %s\n", command.getName(), command.getDescription()));
        }

        String responseMessage = builder.toString();
        logger.info("Команда '{}': вывод справки", getName());

        return new ServerResponse(ServerResponseType.SUCCESS, responseMessage);
    }
}