package yaart.s468198.base.exceptions;

/**
 * InvalidArgumentsException - исключение, когда аргумент не соответствует ожидаемому типу или не проходит ограничения
 */

public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException(String message) {
        super(message);
    }
}
