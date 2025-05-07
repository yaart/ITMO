package yaart.s468198.base.exceptions;

import java.io.Serializable;

/**
 * RecursionException - выбрасывается при обнаружении рекурсивного вызова скрипта.
 */
public class RecursionException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public RecursionException(String message) {
        super(message);
    }
}