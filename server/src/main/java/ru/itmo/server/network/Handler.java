package ru.itmo.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.dao.UserDAO;
import ru.itmo.general.models.User;

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
            User user;
            if (request.getLogin() != null) {
                user = userDAO.getUserByUsername(request.getLogin());
            } else {
                user = null;
            }
            if (user == null && !"register".equals(request.getCommand()) && !"login".equals(request.getCommand())) {
                Response response = new Response(false, "Вы не вошли в систему." + '\n' +
                        "Введите register для регистрации или login для входа");
                sendResponse(clientSocketChannel, response);
            } else {
                if (user != null && userDAO.verifyUserPassword(user, request.getPassword()))
                    request.setUserId(user.getId());
                Response response = CommandManager.handle(request);
                sendResponse(clientSocketChannel, response);
            }
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage());
            Response response = new Response(false, "Invalid request");
            sendResponse(clientSocketChannel, response);
        }
        // Set interest back to OP_READ after parsing is complete
        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
        // Wake up the selector to update interest operations
        key.selector().wakeup();
    }
}
