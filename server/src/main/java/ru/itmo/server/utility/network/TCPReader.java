package ru.itmo.server.utility.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.dao.UserDAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * A runnable task for reading incoming requests from a client's socket channel.
 * It reads data from the channel, parses it, and delegates further processing to a handler.
 *
 * @author zevtos
 */
public class TCPReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger("TCPReader");
    private static final UserDAO userDAO = new UserDAO();
    private final SelectionKey key;

    /**
     * Constructs a TCPReader with the given selection key.
     *
     * @param key The selection key associated with the client's socket channel.
     */
    public TCPReader(SelectionKey key) {
        this.key = key;
    }

    /**
     * Reads data from the client's socket channel and delegates further processing to a handler.
     * If parsing is complete, it sets the interest back to OP_READ and wakes up the selector.
     */
    @Override
    public void run() {
        if (!readRequest(key)) {
            // Set interest back to OP_READ after parsing is complete
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            // Wake up the selector to update interest operations
            key.selector().wakeup();
        }
    }

    /**
     * Reads the incoming request from the client's socket channel.
     *
     * @param key The selection key associated with the client's socket channel.
     * @return true if the reading and parsing of the request is successful, false otherwise.
     */
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
                // Connection closed by client
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
        // Start a new handler to process the request
        new Handler(clientSocketChannel, byteArrayOutputStream, key, userDAO).start();
        return true;
    }
}
