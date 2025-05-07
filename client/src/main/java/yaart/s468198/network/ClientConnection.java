package yaart.s468198.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * ClientConnection - управление сетевым соединением с сервером.
 */
public class ClientConnection {
    private static final Logger logger = LogManager.getLogger(ClientConnection.class);
    private SocketChannel channel;

    /**
     * Подключение к серверу
     *
     * @param host     адрес сервера
     * @param port     порт сервера
     * @throws IOException если не удалось подключиться
     */
    public void connect(String host, int port) throws IOException {
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));

        // Ожидаем завершения подключения
        while (!channel.finishConnect()) {
            try {
                Thread.sleep(100); // Ждём подключения
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Передаём прерывание
                throw new IOException("Прервано ожидание подключения");
            }
        }

        logger.info("Подключение к серверу {}:{} установлено", host, port);
    }

    /**
     * Отправляет объект на сервер
     *
     * @param object объект для отправки
     * @throws IOException если произошла ошибка при записи
     */
    public void send(Object object) throws IOException {
        if (channel == null || !channel.isConnected()) {
            throw new IOException("Соединение с сервером закрыто");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(channel.socket().getOutputStream())) {
            oos.writeObject(object);
            oos.flush();
        }
    }

    /**
     * Получает объект от сервера
     *
     * @return объект, полученный от сервера
     * @throws IOException          если произошла ошибка при чтении
     * @throws ClassNotFoundException если объект невозможно десериализовать
     */
    public Object receive() throws IOException, ClassNotFoundException {
        if (channel == null || !channel.isConnected()) {
            throw new IOException("Соединение с сервером закрыто");
        }

        try (ObjectInputStream ois = new ObjectInputStream(channel.socket().getInputStream())) {
            return ois.readObject();
        }
    }

    /**
     * Закрывает соединение
     *
     * @throws IOException если произошла ошибка при закрытии
     */
    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
            logger.info("Соединение с сервером закрыто");
        }
    }
}