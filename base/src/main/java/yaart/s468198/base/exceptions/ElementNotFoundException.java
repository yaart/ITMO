package yaart.s468198.base.exceptions;

/**
 * ElementNotFoundException - исключение, когда элемент не был найден в коллекции
 */

public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String message) {
        super(message);
    }
}