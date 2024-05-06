package ru.itmo.server.utility.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A TCP server that listens for incoming connections and handles them asynchronously.
 *
 * @author zevtos
 */
public class TCPServer {
    private static final Logger logger = LoggerFactory.getLogger("TCPServer");
    private final int port;
    private final ExecutorService threadPool;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    /**
     * Constructs a TCP server with the specified port.
     *
     * @param port The port on which the server will listen for incoming connections.
     */
    public TCPServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    /**
     * Starts the TCP server, initializing the server socket channel and handling incoming connections.
     */
    public void start() {
        initServerSocketChannel();

        // Main server loop
        while (!Thread.currentThread().isInterrupted()) {
            select();  // Blocks until events occur or until the reading thread wakes up to add an interesting key
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    handleAccept();
                } else if (key.isReadable()) {
                    // Read events are handled asynchronously by submitting tasks to the thread pool
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                    threadPool.submit(new TCPReader(key));
                }
            }
            selector.selectedKeys().clear(); // Clears processed keys
        }
    }

    /**
     * Waits for events on the registered channels
     */
    private void select() {
        try {
            selector.select();
        } catch (Exception e) {
            logger.error("Error selecting thread: {}", e.getMessage());
        }
    }

    /**
     * Initializes the server socket channel and registers it with the selector
     */
    private void initServerSocketChannel() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Server started on port {}", port);
        } catch (ClosedChannelException e) {
            logger.error("channel closed: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error opening server socket: {}", e.getMessage());
        }
    }

    /**
     * Handles an incoming connection request
     */
    private void handleAccept() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            if (client != null) {
                // Configure the client channel as non-blocking and register it with the selector for read events
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
                logger.info("New connection: {}", client.getRemoteAddress());
            }
        } catch (IOException e) {
            logger.error("Error accepting connection: {}", e.getMessage());
        }
    }
}
