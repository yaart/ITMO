package yaart.s468198.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yaart.s468198.base.response.ClientCommandRequest;
import yaart.s468198.base.response.ServerResponse;
import yaart.s468198.util.DeserializationUtil;
import yaart.s468198.util.SerializationUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * NetworkClient - управление сетевым подключением к серверу.
 */
public class NetworkClient {
    private static final Logger logger = LogManager.getLogger(NetworkClient.class);
    private final String host;
    private final int port;
    private SocketChannel channel;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Подключение к серверу
     */
    public void connect() throws IOException {
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(true);
            boolean connected = channel.connect(new InetSocketAddress(host, port));
            if (!connected) {
                throw new IOException("Не удалось подключиться к " + host + ":" + port);
            }
            logger.info("Подключение к серверу {}:{}", host, port);
        } catch (IOException e) {
            logger.error("Ошибка подключения: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Отправка команды серверу
     */
    public void send(ClientCommandRequest request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();

        ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    /**
     * Получение ответа от сервера
     */
    public ServerResponse receive() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        channel.read(buffer);
        buffer.flip();

        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(buffer.array(), 0, buffer.limit())
        );

        return (ServerResponse) ois.readObject();
    }


    /**
     * Закрытие соединения
     */
    public void close() throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.close();
            logger.info("Соединение с сервером закрыто");
        }
    }
}