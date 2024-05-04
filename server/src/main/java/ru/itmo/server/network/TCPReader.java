package ru.itmo.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

import static ru.itmo.server.network.TCPWriter.sendResponse;

public class TCPReader implements Callable<Response> {
    private static Logger logger = LoggerFactory.getLogger("TCPReader");
    private final SelectionKey key;

    public TCPReader(SelectionKey key) {
        this.key = key;
    }

    @Override
    public Response call() {
        Response response = parseRequest(key);
        // Set interest back to OP_READ after parsing is complete
        key.interestOps(key.interestOps() | SelectionKey.OP_READ);

        // Wake up the selector to update interest operations
        key.selector().wakeup();

        return response;
    }


    public synchronized Response parseRequest(SelectionKey key) {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192); // Increased buffer size
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        try {
            logger.debug("Reading request from {}", clientSocketChannel.getRemoteAddress());
            while ((bytesRead = clientSocketChannel.read(buffer)) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            if (bytesRead == -1) {
                key.cancel();
                clientSocketChannel.close();
                logger.error("Client disconnected");
                return null;
            }
        } catch (IOException e) {
            logger.error("Error reading data: {}", e.getMessage());
            key.cancel();
            try {
                logger.error("Closing channel: {}", clientSocketChannel.getRemoteAddress());
                clientSocketChannel.close();
            } catch (IOException ce) {
                logger.error("Error closing channel: {}", ce.getMessage());
            }
            return null;
        }

        try {
            byte[] requestBytes = byteArrayOutputStream.toByteArray();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(requestBytes));
            Request request = (Request) objectInputStream.readObject();

            if ("ping".equals(request.getCommand())) {
                Response pingResponse = new Response(true, "Ping successful");
                sendResponse(clientSocketChannel, pingResponse);
                return pingResponse;
            }
            if ("exit".equals(request.getCommand())) {
                logger.info("Client {} terminated", clientSocketChannel.getRemoteAddress());
                clientSocketChannel.close();
                return null;
            }
            Response response = CommandManager.handle(request);
            sendResponse(clientSocketChannel, response);
            return response;
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage());
            Response response = new Response(false, "Invalid request");
            sendResponse(clientSocketChannel, response);
            return response;
        }
    }
}
