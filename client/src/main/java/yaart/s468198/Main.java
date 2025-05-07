package yaart.s468198;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yaart.s468198.exceptions.CouldnotConnectException;
import yaart.s468198.network.NetworkClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main - точка входа клиента.
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        String host = "localhost";
        int port = 8088;

        if (args.length >= 1) {
            host = args[0];
        }

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Неверный порт");
                return;
            }
        }

        logger.info("Запуск клиента");

        // Создание директории для логов
        try {
            Path logDir = Paths.get("logs");
            Files.createDirectories(logDir);
        } catch (IOException e) {
            System.err.println("Не удалось создать папку для логов: " + e.getMessage());
            logger.error("Ошибка создания каталога логов", e);
            return;
        }

        // Подключение к серверу
        NetworkClient networkClient;
        try {
            networkClient = new NetworkClient(host, port);
            networkClient.connect();
        } catch (CouldnotConnectException ex) {
            logger.error(ex.getMessage());
            System.err.println(ex.getMessage());
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Запуск работы клиента
        new Run(networkClient).run();
    }
}