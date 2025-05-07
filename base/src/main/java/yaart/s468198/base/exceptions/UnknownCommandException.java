package yaart.s468198.base.exceptions;

/**
 * CommandArgumentExcetpion - исключение, когда команде были переданы неправильные аргументы
 */

public class UnknownCommandException extends RuntimeException {
    public UnknownCommandException(String message) {
        super(message);
    }
}