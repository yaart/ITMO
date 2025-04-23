package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import java.util.List;

/**
 * Интерфейс для выполнения команд с поддержкой dependency injection
 */
public interface Executable {
    /**
     * Выполняет команду с переданными аргументами
     *
     * @param args аргументы команды (могут быть пустыми)
     * @throws CommandArgumentException если аргументы невалидны
     * @throws Exception                другие ошибки выполнения
     */
    void execute(List<String> args)
            throws CommandArgumentException, Exception;
}
