package yaart.s468198.exceptions;

/**
 * DeserializationException - исключение, когда не удалось десериализовать объект
 */

public class DeserializationException extends RuntimeException {
    public DeserializationException(String message) {
        super(message);
    }
}