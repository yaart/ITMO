
package yaart.s468198.base.response;

import yaart.s468198.base.models.LabWork;
import java.util.List;

/**
 * Ответ сервера, содержащий список лабораторных работ.
 */
public class ServerResponseWithLabWorkList extends ServerResponse {
    private final List<LabWork> labWorkList;

    /**
     * Конструктор.
     *
     * @param type        тип ответа (SUCCESS, ERROR и т.д.)
     * @param message     сообщение от сервера
     * @param labWorkList список лабораторных работ
     * @throws NullPointerException если список равен null
     */
    public ServerResponseWithLabWorkList(ServerResponseType type, String message, List<LabWork> labWorkList) {
        super(type, message);

        if (labWorkList == null) {
            throw new NullPointerException("labWorkList is null");
        }

        this.labWorkList = labWorkList;
    }

    /**
     * Возвращает список лабораторных работ.
     *
     * @return список объектов LabWork
     */
    public List<LabWork> getLabWorkList() {
        return labWorkList;
    }

    /**
     * Строковое представление объекта.
     *
     * @return строка в виде "ServerResponse {type: ..., message: ..., LabWorks: ...}"
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (LabWork labWork : labWorkList) {
            builder.append(labWork.toString()).append("\n");
        }
        return "ServerResponse {type: " + getType() + ", message: " + getMessage() +
                ", LabWorks:\n" + builder.toString() + "}";
    }
}