package yaart.s468198.base.exceptions;

/**
 * InvalidArgumentsException - исключение, когда скрипт не удается выполнить
 */
public class ScriptException extends Exception {
    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }
}