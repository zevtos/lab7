package ru.itmo.client.network;

import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;

import ru.itmo.general.utility.console.Console;

public class TCPClient {
    private final String serverAddress;
    private final int serverPort;
    private SocketChannel socketChannel;
    private final Console console;

    public TCPClient(String serverAddress, int serverPort, Console console) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.console = console;
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
                            System.out.println("Подключено к серверу: " + serverAddress + ":" + serverPort);
                            return true;
                        }
                    }
                }
            }
            throw new TimeoutException("Не удалось подключиться в течение 10 секунд");
        } catch (IOException e) {
            System.out.println("Ошибка при подключении к серверу: " + e.getMessage());
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
                    System.out.println("Ошибка при закрытии ресурсов: " + e.getMessage());
                }
            }
        }
    }


    public boolean ensureConnection() {
        if (!isConnected()) {
            console.println("Нет подключения к серверу.");
            try {
                console.println("Попытка повторного подключения к серверу...");
                connect();
            } catch (TimeoutException e) {
                console.printError(getClass(), "Ошибка переподключения: " + e.getMessage());
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
        socketChannel.register(selector, socketChannel.validOps());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 10000) { // Ожидаем ответ не больше 10 секунд
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int bytesRead;
            while ((bytesRead = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, bytesRead);
                buffer.clear();
            }

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            if (responseBytes.length > 0) {
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(responseBytes));
                return (Response) objectInputStream.readObject();
            }
        }

        return null;
    }


    public Response sendCommand(Request request) {
        try {
            sendRequest(request);
            return receiveResponse();
        } catch (IOException | ClassNotFoundException ignored) {
        }
        console.println("Повторная попытка отправки");
        try {
            sendCommand(request);
            return receiveResponse();
        } catch (IOException | ClassNotFoundException ignored) {
            console.printError(getClass(), "Запрос не отправлен. Повторите попытку позже.");
        }
        try {
            disconnect();
        } catch (IOException e) {
            console.printError(getClass(), "Не удалось закрыть соединение");
        }
        return new Response(false, "Команда не выполнена!", null);
    }

    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

}
