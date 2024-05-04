package ru.itmo.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.network.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPWriter {
    private static final Logger logger = LoggerFactory.getLogger("TCPWriter");

    public static void sendResponse(SocketChannel clientSocketChannel, Response response) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                logger.debug("Отправка ответа клиенту {}", clientSocketChannel.getRemoteAddress());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
            } catch (IOException e) {
                logger.error("Ошибка при сериализации ответа: {}", e.getMessage());
                throw e;
            }

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(responseBytes);
            while (buffer.hasRemaining()) {
                try {
                    clientSocketChannel.write(buffer);
                } catch (IOException e) {
                    logger.error("Ошибка при отправке данных клиенту: {}", e.getMessage());
                    throw e;
                }
            }
        } catch (IOException e) {
            // Здесь можно добавить дополнительную логику, например, попытку переотправить ответ или закрыть соединение
            logger.error("Неустранимая ошибка при отправке ответа: {}", e.getMessage());
        }
    }
}
