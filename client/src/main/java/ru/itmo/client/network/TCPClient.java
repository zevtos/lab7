package ru.itmo.client.network;

import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TCPClient {
    private final String serverAddress;
    private final int serverPort;
    private SocketChannel socketChannel;
    private Response response;
    private final Object responseLock = new Object();
    private boolean responseReceived;

    public TCPClient(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(serverAddress, serverPort));

        while (!socketChannel.finishConnect()) {
            // Waiting for connection to be established
        }
        System.out.println("Подключено к серверу: " + serverAddress + ":" + serverPort);
    }

    public void disconnect() throws IOException {
        if (socketChannel != null) {
            socketChannel.close();
        }
    }

    public void sendRequest(Request request) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();
        byte[] requestBytes = byteArrayOutputStream.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(requestBytes);
        socketChannel.write(buffer);
    }

    public Response receiveResponse() throws IOException, ClassNotFoundException {
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
            waitForResponse(); // Ждем получения ответа от сервера
            System.out.println(response);
            return response;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при отправки запроса серверу");
        }
        return new Response(false, "exit", null);
    }

    private void waitForResponse() throws IOException, ClassNotFoundException {
        synchronized (responseLock) {
            response = receiveResponse();
            responseLock.notifyAll();
        }
    }
}
