package yaart.s468198.util;

import yaart.s468198.base.response.ServerResponse;
import java.io.*;
import java.nio.ByteBuffer;

/**
 * DeserializationUtil - утилиты для десериализации ответов.
 */
public class DeserializationUtil {
    public static ServerResponse deserializeServerResponse(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (ServerResponse) ois.readObject();
    }
}