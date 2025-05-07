package yaart.s468198;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;

/**
 * Main - точка входа серверного приложения.
 */
public class Main {
    private static final Logger logger = LogManager.getRootLogger();

    public static void main(String[] args) {
        int port = 8088;
        String collectionFilePath = "./data/collection.csv";

        if (args.length >= 1) {
            collectionFilePath = args[0];
        }

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                logger.warn("Неверный формат порта '{}', используется стандартный порт: {}", args[1], port);
            }
        }

        File fileDir = new File(collectionFilePath).getParentFile();
        if (fileDir != null && !fileDir.exists()) {
            boolean created = fileDir.mkdirs();
            if (!created) {
                logger.error("Не удалось создать каталог для файла коллекции");
                System.exit(1);
            }
        }

        try {
            Run run = new Run(port, collectionFilePath);
            run.start();
        } catch (IOException e) {
            logger.error("Не удалось запустить сервер: {}", e.getMessage());
            System.exit(1);
        }
    }
}