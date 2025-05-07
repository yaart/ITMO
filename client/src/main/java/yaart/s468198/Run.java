package yaart.s468198;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import yaart.s468198.base.exceptions.UnknownCommandException;
import yaart.s468198.base.iomanager.IOManager;
import yaart.s468198.base.iomanager.StandartIOManager;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.exceptions.ServerErrorResponseException;
import yaart.s468198.manager.ClientCommandManager;
import yaart.s468198.manager.ClientCommandManagerSetuper;
import yaart.s468198.network.NetworkClient;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Run - класс для запуска клиентской части программы.
 */
public class Run {
    private static final Logger logger = LogManager.getRootLogger();
    private final ClientCommandManager commandManager;
    private final IOManager ioManager;
    private final NetworkClient networkClient;

    /**
     * Конструктор.
     *
     * @param networkClient сетевой клиент
     */
    public Run(NetworkClient networkClient) {
        this.commandManager = new ClientCommandManager();
        this.ioManager = new StandartIOManager();
        this.networkClient = networkClient;
        setup();
    }

    /**
     * Настройка команд.
     */
    private void setup() {
        logger.info("Настройка команд");
        ClientCommandManagerSetuper.setupCommandManager(networkClient, ioManager, commandManager);
    }

    /**
     * Запуск основного цикла работы клиента.
     */
    public void run() {
        logger.info("Запуск цикла работы клиента");
        while (true) {
            try {
                ioManager.write("> ");
                String input = ioManager.readLine().trim();

                if ("exit".equalsIgnoreCase(input)) {
                    ioManager.writeLine("Выход из программы...");
                    break;
                }

                ServerResponse response = commandManager.execute(input);
                ioManager.writeLine("[SERVER] " + response.getMessage());

            } catch (NoSuchElementException e) {
                ioManager.writeLine("Конец ввода. Выход...");
                break;
            } catch (UnknownCommandException e) {
                ioManager.writeError("Неизвестная команда: " + e.getMessage());
            } catch (ServerErrorResponseException e) {
                ioManager.writeError("Ошибка сервера: " + e.getMessage());
                if (e.isConnectionError()) {
                    ioManager.writeError("Соединение с сервером разорвано.");
                    break;
                }
            } catch (Exception e) {
                ioManager.writeError("Произошла непредвиденная ошибка: " + e.getMessage());
                logger.error("Ошибка выполнения команды", e);
            }
        }

        try {
            networkClient.close();
        } catch (IOException e) {
            logger.warn("Не удалось корректно закрыть соединение");
        }

        System.exit(0);
    }
}