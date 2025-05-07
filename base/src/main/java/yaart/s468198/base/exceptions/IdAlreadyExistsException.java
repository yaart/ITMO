package yaart.s468198.base.exceptions;

/**
 * IdAlreadyExistsException - исключение, когда при добавлении нового элемента в коллекцию, там уже есть элемент с таким же значением id
 */

public class IdAlreadyExistsException extends RuntimeException {
    public IdAlreadyExistsException(int id) {
        super("LabWork c id " + id + " уже существует");
    }
}