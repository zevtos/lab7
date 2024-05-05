package ru.itmo.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private static final Logger logger = LoggerFactory.getLogger("TCPServer");
    private final int port;
    private final ExecutorService threadPool;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    public TCPServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() {
        initServerSocketChannel();

        while (!Thread.currentThread().isInterrupted()) {
            select();  // Блокирует, пока не появятся события или
            // пока поток чтения не разбудит для добавления интересующего ключа
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    handleAccept();
                } else if (key.isReadable()) {
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                    threadPool.submit(new TCPReader(key));
                }
            }
            selector.selectedKeys().clear(); // Очистка обработанных ключей
        }
    }

    private void select() {
        try {
            selector.select();
        } catch (Exception e) {
            logger.error("Error selecting thread: {}", e.getMessage());
        }
    }

    private void initServerSocketChannel() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Сервер запущен на порту {}", port);
        } catch (ClosedChannelException e) {
            logger.error("channel closed: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error opening server socket: {}", e.getMessage());
        }
    }

    private void handleAccept() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            if (client != null) {
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
                logger.info("Новое подключение: {}", client.getRemoteAddress());
            }
        } catch (IOException e) {
            logger.error("Error accepting connection: {}", e.getMessage());
        }
    }
}