package yaart.s468198.managers;

import yaart.s468198.commands.UserCommand;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.*;

/**
 * CommandManager - класс для управления вызовом команд
 */
public class CommandManager {
    private final Map<String, UserCommand> commands = new HashMap<>();

    /**
     * Метод для добавления новой команды
     * @param command новая исполняемая команда
     */
    public void addCommand(UserCommand command) {
        commands.put(command.getName(), command);
    }

    /**
     * Метод для исполнения команды
     * @param input строка с именем команды и её аргументами
     */
    public void executeCommand(String input, IOManager ioManager) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0];

        UserCommand command = commands.get(commandName);
        if (command == null) {
            ioManager.writeError("Неизвестная команда: " + commandName);
            return;
        }

        try {
            String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];
            command.execute(List.of(args));
        } catch (Exception e) {
            ioManager.writeError("Ошибка выполнения: " + e.getMessage());
        }
    }

    /**
     * Получение всех команд в отсортированном по имени порядке
     * @return коллекция команд
     */
    public Collection<UserCommand> getCommands() {
        return commands.values();
    }
}