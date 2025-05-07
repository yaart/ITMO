
package yaart.s468198.base.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * ClientResponse - класс, отвечающий за передачу команд и их аргументов на сервер
 */

public class ClientCommandRequest implements Serializable {
    private final String commandName;
    private final List<Serializable> arguments;

    public ClientCommandRequest(String commandName, List<Serializable> arguments) {
        this.commandName = commandName;
        this.arguments = arguments;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<Serializable> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ClientCommandRequest {commandName: ").append(commandName).append(", arguments: ");
        for (Serializable argument : arguments) {
            builder.append(", ").append(argument);
        }

        return builder.append("}").toString();
    }
}