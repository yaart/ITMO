package yaart.s468198.server.util;

import yaart.s468198.base.response.ServerResponse;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * SerializationUtil - класса-утилита для сериализации ответов сервера.
 */

public class SerializationUtil {
    public static ByteBuffer serializeServerResponse(ServerResponse serverResponse) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(serverResponse);
        oos.close();
        return ByteBuffer.wrap(baos.toByteArray());
    }
}