package ru.itmo.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.dao.UserDAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TCPReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger("TCPReader");
    private final SelectionKey key;
    private static final UserDAO userDAO = new UserDAO();

    public TCPReader(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        if (!readRequest(key)) {
            // Set interest back to OP_READ after parsing is complete
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            // Wake up the selector to update interest operations
            key.selector().wakeup();
        }
    }

    public boolean readRequest(SelectionKey key) {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
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
                return false;
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
            return false;
        }
        new Handler(clientSocketChannel, byteArrayOutputStream, key, userDAO).start();
        return true;
    }
}
