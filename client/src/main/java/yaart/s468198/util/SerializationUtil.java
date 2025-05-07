package yaart.s468198.util;

import yaart.s468198.base.response.ClientCommandRequest;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * SerializationUtil - утилиты для сериализации команд.
 */
public class SerializationUtil {
    public static ByteBuffer serializeClientCommand(ClientCommandRequest request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();
        return ByteBuffer.wrap(baos.toByteArray());
    }
}