package yaart.s468198.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaart.s468198.base.response.ClientCommandRequest;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.base.response.ServerResponseType;
import yaart.s468198.managers.CollectionManager;
import yaart.s468198.managers.ServerCommandManager;
import yaart.s468198.base.parser.CsvCollectionManager;
import yaart.s468198.server.util.DeserializationUtil;
import yaart.s468198.server.util.SerializationUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

/**
 * Server - класс, реализующий TCP-сервер
 */
public class Server {
    private final Logger logger = LogManager.getRootLogger();
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final ServerCommandManager commandManager;
    private final CollectionManager collectionManager;

    /**
     * Конструктор сервера.
     *
     * @param commandManager    менеджер команд
     * @param collectionManager менеджер коллекции
     * @param port              порт сервера
     * @throws IOException если не удалось открыть сокет
     */
    public Server(ServerCommandManager commandManager,
                  CollectionManager collectionManager,int port
                  ) throws IOException {
        this.commandManager = commandManager;
        this.collectionManager = collectionManager;
        this.selector = Selector.open();
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress("0.0.0.0", port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        logger.info("Сервер запущен на порту {}", port);
    }

    /**
     * Запускает сервер.
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        try {
            while (!Thread.currentThread().isInterrupted()) {
                int readyKeys = selector.select();

                if (readyKeys == 0) continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        acceptConnection(key);
                    } else if (key.isReadable()) {
                        processClientRequest(key);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка в основном цикле сервера: {}", e.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Принимает новое соединение и регистрирует его на чтение.
     */
    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();

        if (clientChannel != null) {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            logger.info("Новое подключение: {}", clientChannel.getRemoteAddress());
        }
    }

    /**
     * Обрабатывает запрос клиента.
     */
    private void processClientRequest(SelectionKey key) {
        try {
            getClientRequest(key);
        } catch (IOException e) {
            logger.error("Ошибка при обработке запроса: {}", e.getMessage());
        }
    }

    /**
     * Получает запрос от клиента.
     */
    private ClientRequestWithIP getClientRequest(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        InetSocketAddress address = (InetSocketAddress) channel.getRemoteAddress();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int bytesRead = channel.read(buffer);

        if (bytesRead == -1) {
            logger.warn("Клиент отключился: {}", address.getHostString());
            channel.close();
            return null;
        }

        buffer.flip();

        ClientCommandRequest request;
        try {
            request = DeserializationUtil.deserializeClientRequest(buffer);
        } catch (ClassNotFoundException | IOException e) {
            logger.error("Не удалось десериализовать команду: {}", e.getMessage());
            send(channel, new ServerResponse(ServerResponseType.CORRUPTED, "Ошибка десериализации"));
            buffer.clear();
            return null;
        }

        logger.info("Получена команда: {}", request.getCommandName());

        ServerResponse response;
        try {
            response = commandManager.execute(request.getCommandName(), request.getArguments());
        } catch (Exception e) {
            logger.error("Ошибка выполнения команды '{}': {}", request.getCommandName(), e.getMessage());
            response = new ServerResponse(ServerResponseType.ERROR, "Ошибка выполнения команды");
        }

        send(channel, response);
        buffer.clear(); // освобождаем буфер

        return new ClientRequestWithIP(request, address);
    }

    /**
     * Отправляет ответ клиенту.
     */
    private void send(SocketChannel channel, ServerResponse response) throws IOException {
        ByteBuffer buffer = SerializationUtil.serializeServerResponse(response);
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.clear();
    }



    /**
     * Корректное завершение работы сервера.
     */
    private void shutdown() {
        try {

            serverChannel.close();
            selector.close();
            logger.info("Сервер остановлен");
        } catch (IOException e) {
            logger.error("Не удалось корректно остановить сервер: {}", e.getMessage());
        }
    }
}