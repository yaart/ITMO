package yaart.s468198.exceptions;

/**
 * ServerErrorResponseException - исключение, когда сервер вернул ошибку.
 */
public class ServerErrorResponseException extends RuntimeException {
    private final boolean connectionError;

    public ServerErrorResponseException(String message, boolean connectionError) {
        super(message);
        this.connectionError = connectionError;
    }

    /**
     * Проверяет, является ли ошибка сетевой (например, отсутствие соединения).
     *
     * @return true, если это ошибка соединения
     */
    public boolean isConnectionError() {
        return connectionError;
    }
}