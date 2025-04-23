package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.models.LabWork;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда для вывода всех элементов коллекции.
 *
 * Класс реализует команду "show", которая позволяет пользователю вывести все элементы коллекции
 * в строковом представлении. Если коллекция пуста, выводится соответствующее сообщение.
 */
public class Show extends UserCommand {
    /**
     * Менеджер коллекции, используемый для получения элементов коллекции.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для получения элементов коллекции
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public Show(CollectionManager collectionManager, IOManager ioManager) {
        super("show", "вывести все элементы коллекции в строковом представлении", ioManager);
        this.collectionManager = collectionManager;
    }

    /**
     * Метод для выполнения команды.
     *
     * @param args аргументы команды (не должны быть указаны для этой команды)
     * @throws CommandArgumentException если переданы аргументы, которые не поддерживаются командой
     */
    @Override
    public void execute(List<String> args) {
        List<LabWork> sortedCollection = collectionManager.getCollection().stream()
                .sorted() // Сортировка по реализации Comparable в LabWork
                .collect(Collectors.toList());

        if (sortedCollection.isEmpty()) {
            ioManager.writeLine("Коллекция пуста.");
            return;
        }

        ioManager.writeLine("Содержимое коллекции (отсортировано):");
        for (LabWork labWork : sortedCollection) {
            ioManager.writeLine(labWork.toString());
        }
    }
}