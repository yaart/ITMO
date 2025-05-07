package yaart.s468198.base.exceptions;

/**
 * CommandArgumentExcetpion - исключение, когда команде были переданы неправильные аргументы
 */

public class CommandArgumentException extends RuntimeException {
    public CommandArgumentException(String message) {
        super(message);
    }
}