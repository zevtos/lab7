package ru.itmo.client.network;

import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.MessageOutput;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class TCPClient {
    private final MessageOutput output;
    private final String serverAddress;
    private final int serverPort;
    private SocketChannel socketChannel;

    public TCPClient(String serverAddress, int serverPort, MessageOutput output) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.output = output;
    }

    public boolean connect() throws TimeoutException {
        Selector selector = null;
        boolean connect_flag = false;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);
            socketChannel.connect(address);

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 10000) {
                if (selector.select(1000) == 0) {
                    continue;
                }

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isConnectable()) {
                        try {
                            connect_flag = socketChannel.finishConnect();
                        } catch (IOException ignored) {
                        }
                        if (connect_flag) {
                            output.println("Подключено к серверу: " + serverAddress + ":" + serverPort);
                            return true;
                        }
                    }
                }
            }
            throw new TimeoutException("Не удалось подключиться в течение 10 секунд");
        } catch (IOException e) {
            output.println("Ошибка при подключении к серверу: " + e.getMessage());
            return false;
        } finally {
            if (!connect_flag) {
                try {
                    if (socketChannel != null) {
                        socketChannel.close();
                    }
                    if (selector != null) {
                        selector.close();
                    }
                } catch (IOException e) {
                    output.println("Ошибка при закрытии ресурсов: " + e.getMessage());
                }
            }
        }
    }


    public boolean ensureConnection() {
        if (!isConnected()) {
            output.println("Нет подключения к серверу.");
            try {
                output.println("Попытка повторного подключения к серверу...");
                connect();
            } catch (TimeoutException e) {
                output.printError("Ошибка переподключения: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public void disconnect() throws IOException {
        if (socketChannel != null) {
            socketChannel.close();
        }
    }

    public void sendRequest(Request request) throws IOException {
        if (!ensureConnection()) throw new IOException();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();
        byte[] requestBytes = byteArrayOutputStream.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(requestBytes);
        socketChannel.write(buffer);
    }

    public Response receiveResponse() throws IOException, ClassNotFoundException {
        ensureConnection();
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        ByteBuffer buffer = ByteBuffer.allocate(16384);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 10000) { // Ожидаем ответ не больше 10 секунд
            int readyChannels = selector.select(10000); // Ожидаем события не больше 10 секунд
            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    int bytesRead;
                    while ((bytesRead = socketChannel.read(buffer)) > 0) {
                        buffer.flip();
                        byteArrayOutputStream.write(buffer.array(), 0, bytesRead);
                        buffer.clear();
                    }
                    if (bytesRead == -1) {
                        // Закрытие канала
                        socketChannel.close();
                    }
                }
                keyIterator.remove();
            }

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            if (responseBytes.length > 0) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(responseBytes))) {
                    return (Response) objectInputStream.readObject();
                } catch (Exception ignore) {
                }
            }
        }

        // Если за 10 секунд не получили ответ, возвращаем null или генерируем исключение
        return null;
    }


    public Response sendCommand(Request request) {
        try {
            sendRequest(request);
            return receiveResponse();
        } catch (IOException | ClassNotFoundException ignored) {
            output.printError(ignored.getMessage());
        }
        output.printError("Запрос не отправлен. Повторите попытку позже.");
        try {
            disconnect();
        } catch (IOException e) {
            output.printError("Не удалось закрыть соединение");
        }
        return new Response(false, "Команда не выполнена!", null);
    }

    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

}



