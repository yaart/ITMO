package yaart.s468198;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.ServerCommandManager;
import yaart.s468198.managers.ServerCommandManagerSetuper;
import yaart.s468198.server.Server;
import java.io.File;
import java.io.IOException;

/**
 * Run - класс для запуска серверного приложения.
 */
public class Run {
    private static final Logger logger = LogManager.getRootLogger();
    private final int port;
    private final String filePath;
    private final CollectionManager collectionManager;
    private final ServerCommandManager commandManager;
    private final Server server;

    /**
     * Конструктор серверного приложения.
     *
     * @param port       порт, на котором будет работать сервер
     * @param filePath   путь к файлу коллекции
     * @throws IOException если не удалось открыть сокет или прочесть/записать файл
     */
    public Run(int port, String filePath) throws IOException {
        this.port = port;
        this.filePath = filePath;
        this.collectionManager = new CollectionManager(filePath);
        this.commandManager = new ServerCommandManager();

        try {
            loadCollection();
        } catch (Exception e) {
            logger.error("Не удалось загрузить коллекцию: {}", e.getMessage());
            System.exit(1);
        }

        ServerCommandManagerSetuper.setupCommandManager(collectionManager, commandManager);

        this.server = new Server(commandManager, collectionManager, port);
    }

    /**
     * Загружает коллекцию LabWork из CSV-файла.
     * @throws Exception если произошла ошибка при чтении или десериализации
     */
    private void loadCollection() throws Exception {
        File file = new File(filePath);

        if (!file.exists()) {
            file.createNewFile();
            logger.info("Файл коллекции создан: {}", filePath);
            return;
        }

        if (file.length() == 0) {
            logger.info("Файл коллекции пуст: {}", filePath);
            return;
        }
    }

    /**
     * Сохраняет коллекцию в файл перед завершением работы сервера.
     */
    public void saveCollection() {
        try {
            collectionManager.saveCollection();
            logger.info("Коллекция сохранена в файл: {}", filePath);
        } catch (IOException e) {
            logger.warn("Ошибка при сохранении коллекции: {}", e.getMessage());
        }
    }

    /**
     * Запускает сервер и добавляет shutdown hook для сохранения коллекции при выходе.
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveCollection));

        logger.info("Сервер запущен. Порт: {}, файл: {}", port, filePath);

        server.start();
    }
}