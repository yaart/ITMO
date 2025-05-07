// yaart/s468198/manager/ClientCommandManager.java
package yaart.s468198.manager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaart.s468198.base.exceptions.UnknownCommandException;
import yaart.s468198.exceptions.ServerErrorResponseException;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.commands.UserCommand;

import java.io.Serializable;
import java.util.*;

/**
 * ClientCommandManager - класс менеджера команд пользователя.
 */
public class ClientCommandManager {
    private final Logger logger = LogManager.getRootLogger();
    private final HashMap<String, UserCommand> commands;

    public ClientCommandManager() {
        this.commands = new HashMap<>();
    }

    /**
     * Добавляет новую команду в менеджер.
     *
     * @param command команда для добавления
     */
    public void addCommand(UserCommand command) {
        addCommand(command.getName(), command);
    }

    /**
     * Добавляет новую команду по имени.
     *
     * @param name имя команды
     * @param command команда
     */
    public void addCommand(String name, UserCommand command) {
        this.commands.put(name.toLowerCase(), command);
    }

    /**
     * Парсит строку ввода на имя команды и аргументы.
     *
     * @param input входная строка
     * @return список, где первый элемент — имя команды, остальные — аргументы
     */
    private List<String> parseInputCommand(String input) {
        input = input.trim();
        return new ArrayList<>(List.of(input.split("\\s+")));
    }

    /**
     * Выполняет команду.
     *
     * @param input строка с командой и аргументами
     * @throws UnknownCommandException если команда не найдена
     * @throws ServerErrorResponseException если сервер вернул ошибку
     */
    public ServerResponse execute(String input) throws UnknownCommandException, ServerErrorResponseException {
        List<String> parts = parseInputCommand(input);

        if (parts.isEmpty()) {
            throw new UnknownCommandException("Пустая команда");
        }

        String commandName = parts.get(0).toLowerCase();
        List<String> arguments = parts.subList(1, parts.size()); // Аргументы без имени команды

        UserCommand executingCommand = this.commands.get(commandName);
        if (executingCommand == null) {
            throw new UnknownCommandException("Неизвестная команда: " + commandName);
        }

        try {
            logger.info("Вызов команды: {}", commandName);

            // Преобразуем аргументы в Serializable
            List<Serializable> serializableArgs = convertArguments(arguments);

            return executingCommand.execute(serializableArgs);
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды {}: {}", commandName, e.getMessage());
            throw new UnknownCommandException("Ошибка выполнения команды: " + e.getMessage());
        }
    }

    /**
     * Преобразует список строковых аргументов в Serializable.
     *
     * @param arguments список строковых аргументов
     * @return список Serializable
     */
    private List<Serializable> convertArguments(List<String> arguments) {
        List<Serializable> serializableArgs = new ArrayList<>();
        for (String arg : arguments) {
            serializableArgs.add(arg); // Пример: просто добавляем строку как Serializable
        }
        return serializableArgs;
    }

    /**
     * Очищает список команд.
     */
    public void clearCommands() {
        logger.info("Очистка списка команд");
        commands.clear();
    }

    /**
     * Возвращает список всех зарегистрированных команд.
     *
     * @return коллекция команд
     */
    public Collection<UserCommand> getCommands() {
        return Collections.unmodifiableCollection(commands.values());
    }
}