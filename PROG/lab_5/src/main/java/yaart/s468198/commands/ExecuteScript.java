package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.exceptions.ScriptException;
import yaart.s468198.managers.CommandManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.script.ScriptManager;

import java.util.List;

/**
 * Команда для выполнения скрипта.
 *
 * Класс реализует команду "execute_script", которая позволяет пользователю
 * выполнить скрипт из указанного файла. Скрипт содержит последовательность команд,
 * которые будут выполнены в порядке их следования в файле.
 */
public class ExecuteScript extends UserCommand {
    /**
     * Менеджер скриптов, используемый для выполнения скриптов.
     */
    private final ScriptManager scriptManager;

    /**
     * Конструктор класса.
     *
     * @param commandManager менеджер команд, который будет использоваться для выполнения команд из скрипта
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public ExecuteScript(CommandManager commandManager, IOManager ioManager) {
        super("execute_script", "execute_script file_name: считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.", ioManager);
        this.scriptManager = new ScriptManager(commandManager);
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args аргументы команды (должен быть указан один аргумент - путь к файлу скрипта)
     * @throws CommandArgumentException если количество аргументов не равно 1
     */
    @Override
    public void execute(List<String> args) {
        if (args.size() != 1) {
            throw new CommandArgumentException("Требуется 1 аргумент - имя файла");
        }

        try {
            // Выполнение скрипта
            scriptManager.executeScript(args.get(0));

            // Уведомление пользователя об успешном выполнении скрипта
            ioManager.writeLine("Скрипт успешно выполнен");
        } catch (ScriptException e) {
            // Обработка ошибок выполнения скрипта
            ioManager.writeError("Ошибка выполнения скрипта: " + e.getMessage());
            if (e.getCause() != null) {
                ioManager.writeError("Причина: " + e.getCause().getMessage());
            }
        }
    }
}