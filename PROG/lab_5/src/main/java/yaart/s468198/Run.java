package yaart.s468198;

import yaart.s468198.exceptions.DeserializationException;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.CommandManager;
import yaart.s468198.managers.CommandManagerSetuper;
import yaart.s468198.managers.iomanager.IOManager;
import yaart.s468198.managers.iomanager.StandartIOManager;
import yaart.s468198.models.LabWork;
import yaart.s468198.parser.CsvCollectionManager;

import java.io.IOException;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Класс для запуска и управления основным циклом программы
 */
public class Run {
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;
    private final IOManager ioManager;
    private final CsvCollectionManager csvManager;

    /**
     * Конструктор класса Run
     * @param filePath путь к файлу с коллекцией
     */
    public Run(String filePath) {
        this.collectionManager = new CollectionManager();
        this.commandManager = new CommandManager();
        this.ioManager = new StandartIOManager();
        this.csvManager = new CsvCollectionManager(filePath);

        initialize();
    }

    /**
     * Инициализация приложения
     */
    private void initialize() {
        loadCollection();
        setupCommands();
        printWelcomeMessage();
    }

    /**
     * Загрузка коллекции из файла
     */
    private void loadCollection() {
        try {
            Collection<LabWork> loadedCollection = csvManager.loadCollection();
            loadedCollection.forEach(collectionManager::addLabWork);
            ioManager.writeLine("Коллекция успешно загружена из файла!");
        } catch (IOException e) {
            ioManager.writeLine("Файл не найден. Создана новая пустая коллекция.");
        } catch (DeserializationException e) {
            ioManager.writeError("Ошибка загрузки коллекции: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Настройка команд
     */
    private void setupCommands() {
        CommandManagerSetuper.setupCommandManager(ioManager, collectionManager, commandManager, csvManager);
    }

    /**
     * Вывод приветственного сообщения
     */
    private void printWelcomeMessage() {
        ioManager.writeLine("====================================");
        ioManager.writeLine("Система управления коллекцией LabWork");
        ioManager.writeLine("Введите 'help' для списка команд");
        ioManager.writeLine("====================================");
    }

    /**
     * Запуск основного цикла программы
     */
    public void run() {
        while (true) {
            try {
                ioManager.write("\n> ");
                String input = ioManager.readLine().trim();
                if (!input.isEmpty()) {
                    commandManager.executeCommand(input, ioManager);
                }
            } catch (NoSuchElementException e) {
                ioManager.writeLine("\nЗавершение работы программы");
                System.exit(0);
            } catch (Exception e) {
                ioManager.writeError("Ошибка: " + e.getMessage());
            }
        }
    }
}