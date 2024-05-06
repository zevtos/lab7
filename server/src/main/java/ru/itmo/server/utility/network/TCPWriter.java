package ru.itmo.server.utility.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.network.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Utility class for sending responses to clients over TCP connections.
 *
 * @author zevtos
 */
public class TCPWriter {
    private static final Logger logger = LoggerFactory.getLogger("TCPWriter");

    /**
     * Sends a response object to the client through the given socket channel.
     *
     * @param clientSocketChannel The socket channel connected to the client.
     * @param response            The response object to be sent.
     */
    public static void sendResponse(SocketChannel clientSocketChannel, Response response) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                logger.debug("Sending response to client {}", clientSocketChannel.getRemoteAddress());
                objectOutputStream.writeObject(response);
                objectOutputStream.flush();
            } catch (IOException e) {
                logger.error("Error serializing response: {}", e.getMessage());
                throw e;
            }

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(responseBytes);
            // Write the response bytes to the channel
            while (buffer.hasRemaining()) {
                try {
                    clientSocketChannel.write(buffer);
                } catch (IOException e) {
                    logger.error("Error sending data to client: {}", e.getMessage());
                    throw e;
                }
            }
        } catch (IOException e) {
            logger.error("Error sending response: {}", e.getMessage());
        }
    }
}
