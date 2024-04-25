package ru.itmo.client.network;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

import ru.itmo.client.utility.console.Console;

public class TCPClient {
    private String serverAddress;
    private int serverPort;
    private SocketChannel socketChannel;
    private Response response;
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
                        } catch (IOException e) {
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


    public void ensureConnection() {
        if (!isConnected()) {
            try {
                console.log("Попытка повторного подключения к серверу...");
                connect();
            } catch (TimeoutException e) {
                console.logError(getClass(), "Ошибка переподключения: " + e.getMessage());
            }
        }
    }

    public void disconnect() throws IOException {
        if (socketChannel != null) {
            socketChannel.close();
        }
    }

    public void sendRequest(Request request) throws IOException {
        ensureConnection();
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

        ByteBuffer buffer = ByteBuffer.allocate(10000);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 10000) { // Ожидаем ответ не больше 10 секунд
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }

            int bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1) {
                break;
            }

            buffer.flip();
            byteArrayOutputStream.write(buffer.array(), 0, bytesRead);
            buffer.clear();

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
            response = receiveResponse(); // Ждем получения ответа от сервера
            return response;
        } catch (IOException | ClassNotFoundException ignored) {
        }
        console.logError(getClass(), "Ошибка при отправки запроса серверу, пожалуйста повторите попытку позже");
        try {
            disconnect();
        } catch (IOException e) {
            console.logError(getClass(), "Не удалось закрыть соединение");
        }
        return new Response(false, "Команда не выполнена!", null);
    }

    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

}

//package ru.itmo.client.network;
//
//import ru.itmo.client.utility.console.Console;
//import ru.itmo.general.network.Request;
//import ru.itmo.general.network.Response;
//
//import java.io.*;
//        import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.SelectionKey;
//import java.nio.channels.Selector;
//import java.nio.channels.SocketChannel;
//import java.util.Iterator;
//import java.util.concurrent.TimeoutException;
//
//public class TCPClient {
//    private final String serverAddress;
//    private final int serverPort;
//    private SocketChannel socketChannel;
//    private Response response;
//    private final Console console;
//
//    public TCPClient(String serverAddress, int serverPort, Console console) {
//        this.serverAddress = serverAddress;
//        this.serverPort = serverPort;
//        this.console = console;
//    }
//
//    public boolean connect() throws TimeoutException {
//        Selector selector = null;
//        try {
//            socketChannel = SocketChannel.open();
//            socketChannel.configureBlocking(false);
//            socketChannel.connect(new InetSocketAddress(serverAddress, serverPort));
//
//            selector = Selector.open();
//            socketChannel.register(selector, SelectionKey.OP_CONNECT);
//
//            long startTime = System.currentTimeMillis();
//            while (System.currentTimeMillis() - startTime < 10000) {
//                if (selector.select(1000) > 0) {
//                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
//                    while (keys.hasNext()) {
//                        SelectionKey key = keys.next();
//                        keys.remove();
//                        if (key.isConnectable() && socketChannel.finishConnect()) {
//                            console.log("Connected to server: " + serverAddress + ":" + serverPort);
//                            return true;
//                        }
//                    }
//                }
//            }
//            throw new TimeoutException("Connection timed out after 10 seconds");
//        } catch (IOException e) {
//            console.logError(getClass(), "Connection error: " + e.getMessage());
//            return false;
//        } finally {
//            try {
//                if (selector != null) {
//                    selector.close();
//                }
//                if (socketChannel != null) {
//                    socketChannel.close();
//                }
//            } catch (IOException e) {
//                console.logError(getClass(), "Resource closing error: " + e.getMessage());
//            }
//        }
//    }
//
//    public void disconnect() throws IOException {
//        if (socketChannel != null && socketChannel.isOpen()) {
//            socketChannel.close();
//            console.log("Disconnected from server.");
//        }
//    }
//
//    public void sendRequest(Request request) throws IOException {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
//            objectOutputStream.writeObject(request);
//            objectOutputStream.flush();
//        }
//
//        ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
//        socketChannel.write(buffer);
//        console.log("Request sent.");
//    }
//
//    public Response receiveResponse() throws IOException, ClassNotFoundException {
//        Selector selector = Selector.open();
//        socketChannel.register(selector, SelectionKey.OP_READ);
//
//        ByteBuffer buffer = ByteBuffer.allocate(1024);
//        try {
//            long startTime = System.currentTimeMillis();
//            while (System.currentTimeMillis() - startTime < 10000) {
//                if (selector.select(1000) > 0) {
//                    int bytesRead = socketChannel.read(buffer);
//                    if (bytesRead == -1) {
//                        throw new IOException("End of stream reached.");
//                    }
//
//                    buffer.flip();
//                    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
//                         ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
//                        return (Response) objectInputStream.readObject();
//                    } finally {
//                        buffer.clear();
//                    }
//                }
//            }
//            throw new TimeoutException("Response timed out after 10 seconds");
//        } finally {
//            selector.close();
//        }
//    }
//
//    public Response sendCommand(Request request) {
//        try {
//            sendRequest(request);
//            response = receiveResponse();
//            console.log("Response received.");
//            return response;
//        } catch (Exception e) {
//            console.logError(getClass(), "Error in sending command: " + e.getMessage());
//            try {
//                disconnect();
//            } catch (IOException ex) {
//                console.logError(getClass(), "Failed to disconnect: " + ex.getMessage());
//            }
//            return new Response(false, "Command not executed!", null);
//        }
//    }
//
//    public boolean isConnected() {
//        return socketChannel != null && socketChannel.isOpen() && socketChannel.isConnected();
//    }
//}
