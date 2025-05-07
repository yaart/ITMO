package yaart.s468198.commands;

import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.exceptions.CommandArgumentException;
import java.io.Serializable;
import java.util.List;

/**
 * UserCommand - абстрактный класс для команд, которые может вызывать пользователь.
 */
public abstract class UserCommand implements Comparable<UserCommand> {
    private final String name;
    private final String description;

    /**
     * Конструктор класса
     * @param name имя команды
     * @param description описание команды
     */
    public UserCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Метод для выполнения команды
     * @param args список аргументов
     * @return ответ сервера
     * @throws CommandArgumentException если количество аргументов некорректно
     */
    public abstract ServerResponse execute(List<Serializable> args) throws CommandArgumentException;

    /**
     * Метод для получения имени команды
     * @return имя команды
     */
    public String getName() {
        return this.name;
    }

    /**
     * Метод для получения описания команды
     * @return описание
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.name;
    }

    /**
     * Метод для сравнения двух команд. Сравнение происходит по имени
     * @param o другой объект
     * @return результат сравнения
     */
    @Override
    public int compareTo(UserCommand o) {
        return this.name.compareTo(o.name);
    }

    /**
     * Проверяет равенство команд по имени
     *
     * @param obj другой объект
     * @return true, если команды равны
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserCommand that = (UserCommand) obj;
        return name.equals(that.name);
    }

    /**
     * Генерирует хеш-код команды на основе её имени
     *
     * @return хеш-код
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}