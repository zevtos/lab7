package ru.itmo.server.network;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

public class TCPServer {
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);

    public TCPServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        logger.info("Сервер запущен на порту {}", port);

        while (!Thread.currentThread().isInterrupted()) {
            selector.select(1000);  // Блокирует, пока не появятся события
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    handleAccept(serverSocketChannel, selector);
                } else if (key.isReadable()) {
                    key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                    threadPool.submit(new TCPReader(key));
                }
            }
            selector.selectedKeys().clear(); // Очистка обработанных ключей
        }
    }

    private void handleAccept(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        if (client != null) {
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            logger.info("Новое подключение: {}", client.getRemoteAddress());
        }
    }

    private void handleRead(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(16384);
        try {
            while (true) {
                buffer.clear();
                int bytesRead = clientChannel.read(buffer);
                if (bytesRead == -1) {
                    key.cancel();
                    clientChannel.close();
                    return; // Закрытие канала, если достигнут конец потока
                }
                if (bytesRead == 0) {
                    break; // Если нет данных для чтения, прекращаем чтение
                }
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
            }
            byte[] data = byteArrayOutputStream.toByteArray();
            if (data.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bis);
                Request request = (Request) ois.readObject();

                threadPool.submit(() -> {
                    Response response = CommandManager.handle(request);
                    try {
                        sendResponse(clientChannel, response);
                    } catch (IOException e) {
                        logger.error("Ошибка при отправке данных", e);
                    } finally {
                        // Возвращение интереса к чтению должно быть здесь
                        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
                        selector.wakeup(); // Пробуждаем селектор для повторной проверки состояния ключей
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                clientChannel.close();
            } catch (IOException ex) {
                logger.error("Ошибка при закрытии канала", ex);
            }
            key.cancel();
            logger.error("Ошибка при чтении данных", e);
        }
    }


    private void sendResponse(SocketChannel channel, Response response) throws IOException {
        logger.debug("Отправка ответа клиенту {}", channel.getRemoteAddress());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();
        ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}


//package ru.itmo.server.network;
//
//
//import java.io.*;
//import java.net.*;
//import java.nio.channels.*;
//import java.util.*;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ru.itmo.server.utility.Runner;
//
//public class TCPServer implements Runnable {
//    private final int port;
//    private ServerSocketChannel serverSocketChannel;
//    private ExecutorService threadPool;
//    private Selector selector;
//    private static final Logger logger = LoggerFactory.getLogger("TCPServer");
//
//    public TCPServer(int port) {
//        this.port = port;
//        this.threadPool = Executors.newFixedThreadPool(10);
//    }
//    public TCPServer(ServerSocketChannel serverSocketChannel, Selector selector) {
//        this.serverSocketChannel = serverSocketChannel;
//        this.selector = selector;
//        this.port = serverSocketChannel.socket().getLocalPort();
//    }
//    @Override
//    public void run() {
//        try {
//            ThreadGroup tcpServerHandlers = new ThreadGroup("TCPServerHandlers");
//            ThreadGroup tcpServerReaders = new ThreadGroup("TCPServerReaders");
//            Thread runner = new Runner(this, tcpServerReaders, tcpServerHandlers);
//            runner.setDaemon(true);
//            runner.start();
//            serverSocketChannel = ServerSocketChannel.open();
//            serverSocketChannel.configureBlocking(false);
//            serverSocketChannel.socket().bind(new InetSocketAddress(port));
//            selector = Selector.open();
//            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//
//            logger.info("Сервер запущен на порту {}", port);
//
//            while (serverSocketChannel.isOpen()) {
//                try {
//                    int readyChannels = selector.select();
//                    if (readyChannels == 0) continue;
//
//                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
//                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
//
//                    while (keyIterator.hasNext()) {
//                        SelectionKey key = keyIterator.next();
//                        keyIterator.remove();
//
//                        if (!key.isValid()) continue;
//
//                        if (key.isAcceptable()) {
//                            acceptConnection();
//                        } else if (key.isReadable()) {
//                            new TCPReader(key).run();
//                        }
//                    }
//                } catch (IOException e) {
//                    if (!(e instanceof ClosedChannelException)) {
//                        logger.error("Ошибка при работе с селектором: {}", e.getMessage());
//                        break;
//                    }
//                }
//            }
//        } catch (IOException e) {
//            logger.error("Ошибка при запуске сервера", e);
//        } catch (RuntimeException e) {
//            logger.info("Завершение работы сервера на порту: {}", e.getMessage());
//            try {
//                stop_server();
//            } catch (IOException exception) {
//                logger.error("Ошибка при остановке сервера", e);
//            }
//        }  finally
//         {
//            try {
//                stop_server();
//                System.exit(0);
//            } catch (IOException e) {
//                logger.error("Ошибка при остановке сервера", e);
//            }
//        }
//    }
//
//    public void stop_server() throws IOException {
//        if (serverSocketChannel != null) {
//            serverSocketChannel.close();
//        }
//        if (selector != null) {
//            selector.close();
//        }
//        if (threadPool != null) {
//            threadPool.shutdown();
//        }
//    }
//
//    private void acceptConnection() throws IOException {
//        SocketChannel clientSocketChannel = serverSocketChannel.accept();
//        clientSocketChannel.configureBlocking(false);
//        clientSocketChannel.register(selector, SelectionKey.OP_READ);
//        logger.info("Новое подключение: {}", clientSocketChannel.getRemoteAddress());
//    }
//}
