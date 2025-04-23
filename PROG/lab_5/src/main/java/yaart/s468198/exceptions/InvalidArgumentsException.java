package yaart.s468198.exceptions;

/**
 * InvalidArgumentsException - исключение, когда аргумент не соответствует ожидаемому типу или не проходит ограничения
 */

public class InvalidArgumentsException extends RuntimeException {
    public InvalidArgumentsException(String message) {
        super(message);
    }
}
