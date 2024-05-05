package ru.itmo.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.User;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.dao.UserDAO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import static ru.itmo.server.network.TCPWriter.sendResponse;

class Handler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger("Handler");
    private final SocketChannel clientSocketChannel;
    private final ByteArrayOutputStream byteArrayOutputStream;
    private final SelectionKey key;
    private final UserDAO userDAO;

    public Handler(
            SocketChannel clientSocketChannel,
            ByteArrayOutputStream byteArrayOutputStream,
            SelectionKey key,
            UserDAO userDAO) {
        this.clientSocketChannel = clientSocketChannel;
        this.byteArrayOutputStream = byteArrayOutputStream;
        this.key = key;
        this.userDAO = userDAO;
    }

    @Override
    public void run() {
        try {
            byte[] requestBytes = byteArrayOutputStream.toByteArray();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(requestBytes));
            Request request = (Request) objectInputStream.readObject();
            if ("exit".equals(request.getCommand())) {
                logger.info("Client {} terminated", clientSocketChannel.getRemoteAddress());
                clientSocketChannel.close();
                return;
            }

            User user = null;
            if (request.getLogin() != null) {
                user = userDAO.getUserByUsername(request.getLogin());
            }

            if (user == null && !"register".equals(request.getCommand()) && !"login".equals(request.getCommand())) {
                // User is not registered and not attempting to login or register
                sendUnauthorizedResponse(clientSocketChannel);
            } else {
                handleRequest(request, user);
            }
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage());
            sendErrorResponse(clientSocketChannel);
        }
        // Set interest back to OP_READ after parsing is complete
        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
        // Wake up the selector to update interest operations
        key.selector().wakeup();
    }

    private void handleRequest(Request request, User user) {
        if (user != null && userDAO.verifyUserPassword(user, request.getPassword())) {
            request.setUserId(user.getId());
        } else if (user != null && !("login".equals(request.getCommand()) || "register".equals(request.getCommand()))) {
            sendUnauthorizedResponse(clientSocketChannel);
        }
        Response response = CommandManager.handle(request);
        sendResponse(clientSocketChannel, response);
    }

    private void sendUnauthorizedResponse(SocketChannel channel) {
        Response response = new Response(false, "Вы не вошли в систему." + '\n' +
                "Введите register для регистрации или login для входа");
        sendResponse(channel, response);
    }

    private void sendErrorResponse(SocketChannel channel) {
        Response response = new Response(false, "Invalid request");
        sendResponse(channel, response);
    }

}
