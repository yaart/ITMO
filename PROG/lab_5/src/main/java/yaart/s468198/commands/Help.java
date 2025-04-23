package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CommandManager;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;
import java.util.Comparator;

/**
 * Команда для вывода списка доступных команд.
 *
 * Класс реализует команду "help", которая позволяет пользователю получить справку
 * о доступных командах. Команды выводятся в алфавитном порядке с их описанием.
 */
public class Help extends UserCommand {
    /**
     * Менеджер команд, используемый для получения списка доступных команд.
     */
    private final CommandManager commandManager;

    /**
     * Конструктор класса.
     *
     * @param commandManager менеджер команд для получения списка доступных команд
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Help(CommandManager commandManager, IOManager ioManager) {
        super("help", "вывести справку по доступным командам", ioManager);
        this.commandManager = commandManager;
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args аргументы команды (не должны быть указаны для этой команды)
     * @throws CommandArgumentException если переданы аргументы, которые не поддерживаются командой
     */
    @Override
    public void execute(List<String> args) {
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Эта команда не принимает аргументы");
        }

        // Вывод заголовка справки
        ioManager.writeLine("\nДоступные команды:");
        ioManager.writeLine("========================================");

        // Получение и сортировка списка команд
        commandManager.getCommands().stream()
                .sorted(Comparator.comparing(UserCommand::getName))
                .forEach(cmd -> ioManager.writeLine(
                        String.format(" %-20s - %s", cmd.getName(), cmd.getDescription())
                ));

        // Вывод завершающей строки
        ioManager.writeLine("========================================\n");
    }
}