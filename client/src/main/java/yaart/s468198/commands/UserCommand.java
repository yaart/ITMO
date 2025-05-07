package yaart.s468198.commands;

import yaart.s468198.base.exceptions.CommandArgumentException;
import yaart.s468198.base.response.ClientCommandRequest;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.network.NetworkClient;
import yaart.s468198.base.response.ServerResponse;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * UserCommand - абстрактный класс команд пользователя.
 */
public abstract class UserCommand implements Comparable<UserCommand> {
    protected final String name;
    protected final String description;
    protected final IOManager ioManager;
    protected final NetworkClient networkClient;

    public UserCommand(String name, String description, IOManager ioManager, NetworkClient networkClient) {
        this.name = name;
        this.description = description;
        this.ioManager = ioManager;
        this.networkClient = networkClient;
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args список аргументов команды
     * @return ответ от сервера
     * @throws CommandArgumentException если количество или тип аргументов неверны
     */
    public abstract ServerResponse execute(List<Serializable> args) throws CommandArgumentException, IOException, InterruptedException, ClassNotFoundException;

    /**
     * Сравнивает команды по имени.
     *
     * @param other другая команда
     * @return результат сравнения
     */
    @Override
    public int compareTo(UserCommand other) {
        return this.name.compareTo(other.name);
    }

    /**
     * Возвращает имя команды.
     *
     * @return имя команды
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает описание команды.
     *
     * @return описание команды
     */
    public String getDescription() {
        return description;
    }

    /**
     * Отправляет запрос на сервер.
     *
     * @param commandName имя команды
     * @param arguments   аргументы команды
     * @return ответ от сервера
     * @throws IOException если не удалось отправить/получить данные
     */
    protected ServerResponse sendAndReceive(String commandName, List<Serializable> arguments) throws IOException, ClassNotFoundException {
        ClientCommandRequest request = new ClientCommandRequest(commandName, arguments);

        networkClient.send(request);
        return networkClient.receive();
    }
}