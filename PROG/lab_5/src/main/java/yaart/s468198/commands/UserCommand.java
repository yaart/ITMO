package yaart.s468198.commands;

import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;

/**
 * Абстрактный класс для пользовательских команд.
 *
 * Этот класс предоставляет базовую реализацию для всех пользовательских команд.
 * Каждая команда должна быть представлена как подкласс этого класса и реализовать метод {@link #execute(List)}.
 */
public abstract class UserCommand implements Comparable<UserCommand> {
    /**
     * Имя команды (например, "add", "remove", "help").
     */
    protected final String name;

    /**
     * Описание команды, используемое для справки.
     */
    protected final String description;

    /**
     * Менеджер ввода-вывода для взаимодействия с пользователем.
     */
    protected final IOManager ioManager;

    /**
     * Конструктор класса.
     *
     * @param name имя команды
     * @param description описание команды
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public UserCommand(String name, String description, IOManager ioManager) {
        this.name = name;
        this.description = description;
        this.ioManager = ioManager;
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
     * Выполняет команду с заданными аргументами.
     *
     * @param args аргументы команды
     */
    public abstract void execute(List<String> args);

    /**
     * Сравнивает команды по их имени.
     *
     * @param other другая команда для сравнения
     * @return результат сравнения имен команд
     */
    @Override
    public int compareTo(UserCommand other) {
        return this.name.compareTo(other.name);
    }

    /**
     * Проверяет, равна ли текущая команда другой команде.
     * Две команды считаются равными, если их имена совпадают.
     *
     * @param obj объект для сравнения
     * @return true, если команды равны, и false в противном случае
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserCommand that = (UserCommand) obj;
        return name.equals(that.name);
    }

    /**
     * Возвращает хеш-код команды.
     * Хеш-код вычисляется на основе имени команды.
     *
     * @return хеш-код команды
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}