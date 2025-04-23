package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;

/**
 * Команда для выхода из программы.
 *
 * Класс реализует команду "exit", которая позволяет пользователю завершить выполнение программы.
 * Важно отметить, что данная команда не сохраняет данные в файл перед завершением.
 */
public class Exit extends UserCommand {

    /**
     * Конструктор класса.
     *
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Exit(IOManager ioManager) {
        super("exit", "завершить программу (без сохранения в файл)", ioManager);
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

        // Завершение программы
        System.exit(0);
    }
}