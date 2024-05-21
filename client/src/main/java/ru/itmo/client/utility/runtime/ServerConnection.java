package ru.itmo.client.utility.runtime;

import lombok.Getter;
import lombok.Setter;
import ru.itmo.client.network.TCPClient;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.gui.GuiMessageOutput;

import javax.swing.*;
import java.util.List;

public class ServerConnection {
    private TCPClient tcpClient;
    @Setter
    private String login;
    @Setter
    private String password;
    @Getter
    @Setter
    private Integer currentUserId;

    public ServerConnection(String host, int port) {
        this.tcpClient = new TCPClient(host, port, new GuiMessageOutput(new JTextArea()));
    }

    public Response sendCommand(String[] userCommand) {
        Request request;
        if (userCommand[0].isEmpty()) return new Response(false, "UserCommand is empty");
        var command = CommandManager.getCommands().get(userCommand[0]);

        if (command == null) {
            return new Response(false, "Команда '" + userCommand[0] + "' не найдена. Наберите 'help' для справки");
        }

        request = command.execute(userCommand);
        return sendCommand(request);
    }

    public Response sendCommand(String command, Object data) {
        try {
            Request request = new Request(command, data);
            return sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Response sendCommand(Request request) {
        if (request.getLogin() == null) {
            request.setLogin(login);
            request.setPassword(password);
        } else {
            login = request.getLogin();
            password = request.getPassword();
        }
        Response response = null;
        try {
            response = tcpClient.sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(request.getCommand().equals("login") || request.getCommand().equals("register")) {
            try {
                currentUserId = (Integer) response.getData();
            } catch (Exception ignored){
            }
        }
        return response;
    }

    public List<Ticket> receiveTickets() {
        try {
            System.out.println("Ticekts");
            Response response = sendCommand("show", null);
            return (List<Ticket>) response.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
