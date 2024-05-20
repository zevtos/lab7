package ru.itmo.client.utility.runtime;

import lombok.Setter;
import ru.itmo.client.network.TCPClient;
import ru.itmo.general.commands.core.Show;
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
    public ServerConnection(String host, int port) {
        this.tcpClient = new TCPClient(host, port, new GuiMessageOutput(new JTextArea()));
    }

    public Response sendCommand(String command, Object data) {
        try {
            Request request = new Request(command, data);
            System.out.println(request);
            if(request.getCommand().equals("login") || request.getCommand().equals("register")){
                System.out.println("login: " + login + "   password: " + password);
                login = request.getLogin();
                password = request.getPassword();
            }
            return sendCommand(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response sendCommand(Request request) {
        if(request.getLogin() == null){
            request.setLogin(login);
            request.setPassword(password);
            System.out.println(login);
            System.out.println(password);
        }
        try {
            Response response = tcpClient.sendCommand(request);
            System.out.println(response);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ticket> receiveTickets() {
        try {
            System.out.println("Ticekts");
            Response response = sendCommand("show", null);
            System.out.println("Ticekts");
            System.out.println(response.getData().toString());
            return (List<Ticket>) response.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
