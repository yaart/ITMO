package yaart.s468198.commands;

import yaart.s468198.exceptions.CommandArgumentException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.iomanager.IOManager;

import java.util.List;
import java.util.Set;

/**
 * Команда для вывода уникальных значений поля tunedInWorks.
 *
 * Класс реализует команду "print_unique_tuned_in_works", которая позволяет пользователю
 * вывести все уникальные значения поля tunedInWorks из элементов коллекции.
 */
public class PrintUniqueTunedInWorks extends UserCommand {
    /**
     * Менеджер коллекции, используемый для получения уникальных значений поля tunedInWorks.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager менеджер коллекции для получения уникальных значений tunedInWorks
     * @param ioManager менеджер ввода-вывода для взаимодействия с пользователем
     */
    public PrintUniqueTunedInWorks(CollectionManager collectionManager, IOManager ioManager) {
        super("print_unique_tuned_in_works",
                "вывести уникальные значения поля tunedInWorks всех элементов в коллекции",
                ioManager);
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
        if (!args.isEmpty()) {
            throw new CommandArgumentException("Эта команда не принимает аргументы");
        }

        // Получение уникальных значений поля tunedInWorks
        Set<Integer> uniqueValues = collectionManager.getUniqueTunedInWorks();

        // Вывод результата пользователю
        if (uniqueValues.isEmpty()) {
            ioManager.writeLine("Нет уникальных значений tunedInWorks");
        } else {
            ioManager.writeLine("Уникальные значения tunedInWorks:");
            uniqueValues.forEach(value -> ioManager.writeLine(" " + value));
        }
    }
}