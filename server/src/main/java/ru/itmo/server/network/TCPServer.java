package ru.itmo.server.network;

import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.CommandManager;
import ru.itmo.server.managers.collections.TicketCollectionManager;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

public class TCPServer extends Thread {
    private final int port;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private final CommandManager commandManager;
    private static final Logger logger = LoggerFactory.getLogger(TCPServer.class);

    public TCPServer(int port, TicketCollectionManager ticketCollectionManager) {
        this.port = port;
        this.commandManager = new CommandManager(ticketCollectionManager);
    }

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            logger.info("Сервер запущен на порту {}", port);

            while (!Thread.currentThread().isInterrupted() && serverSocketChannel.isOpen()) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) continue;

                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (!key.isValid()) continue;

                        if (key.isAcceptable()) {
                            acceptConnection();
                        } else if (key.isReadable()) {
                            readRequest(key);
                        }
                    }
                } catch (IOException e) {
                    if (!(e instanceof ClosedChannelException)) {
                        logger.error("Ошибка при работе с селектором: {}", e.getMessage());
                        break;
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка при запуске сервера", e);
        } finally {
            try {
                stop_server();
            } catch (IOException e) {
                logger.error("Ошибка при остановке сервера", e);
            }
        }
    }

    public void stop_server() throws IOException {
        if (serverSocketChannel != null) {
            serverSocketChannel.close();
            selector.close();
        }
    }

    private void acceptConnection() throws IOException {
        SocketChannel clientSocketChannel = serverSocketChannel.accept();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.register(selector, SelectionKey.OP_READ);
        logger.info("Новое подключение: {}", clientSocketChannel.getRemoteAddress());
    }

    private void readRequest(SelectionKey key) {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(4096); // Увеличенный размер буфера
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead;
        try {
            logger.debug("Чтение запроса от {}", clientSocketChannel.getRemoteAddress());
            while ((bytesRead = clientSocketChannel.read(buffer)) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            if (bytesRead == -1) {
                key.cancel();
                clientSocketChannel.close();
                logger.error("Клиент отключился");
                return;
            }
        } catch (IOException e) {
            logger.error("Ошибка при чтении данных: {}", e.getMessage());
            key.cancel();
            try {
                logger.error("Закрытие канала: {}", clientSocketChannel.getRemoteAddress());
                clientSocketChannel.close();
            } catch (IOException ce) {
                logger.error("Ошибка при закрытии канала: {}", ce.getMessage());
            }
            return;
        }

        try {
            byte[] requestBytes = byteArrayOutputStream.toByteArray();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(requestBytes));
            Request request = (Request) objectInputStream.readObject();

            if ("ping".equals(request.getCommand())) {
                Response pingResponse = new Response(true, "Ping successful");
                sendResponse(clientSocketChannel, pingResponse);
                return;
            }
            if("exit".equals(request.getCommand())) {
                logger.info("Клиент {} завершил работу", clientSocketChannel.getRemoteAddress());
                clientSocketChannel.close();
                return;
            }
            Response response = processRequest(request);
            sendResponse(clientSocketChannel, response);
        } catch (Exception e) {
            logger.error("Ошибка при обработке запроса: {}", e.getMessage());
            Response response = new Response(false, "Неверный запрос");
            sendResponse(clientSocketChannel, response);
        }
    }


    private void sendResponse(SocketChannel clientSocketChannel, Response response) {
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


    private Response processRequest(Request request) {
        Response response;
        try {
            response = commandManager.handle(request);
            if ("exit".equals(request.getCommand())) {
                logger.info("Получен запрос на завершение работы сервера.");
                try {
                    stop_server();
                } catch (IOException e) {
                    logger.error("Ошибка при остановке сервера", e);
                }
                System.exit(0);
            }
        } catch (Exception e) {
            response = new Response(false, request.getCommand(), "Команда не найдена!");
        }
        return response;
    }
    private void setSignalProcessing(String... signalNames) {
        for (String signalName : signalNames) {
            try {
                Signal.handle(new Signal(signalName), signal -> {
                    try {
                        this.stop_server();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IllegalArgumentException ignored) {
                // Игнорируем исключение, если сигнал с таким названием уже существует или такого сигнала не существует
            }
        }
    }
}
