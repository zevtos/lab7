package ru.itmo.client.utility.runtime;

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

    public ServerConnection(String host, int port) {
        this.tcpClient = new TCPClient(host, port, new GuiMessageOutput(new JTextArea()));
    }

    public Response sendCommand(String command, Object data) {
        try {
            Request request = new Request(command, data);
            Response response = tcpClient.sendCommand(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response sendCommand(Request request) {
        try {
            Response response = tcpClient.sendCommand(request);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Ticket> receiveTickets() {
        try {
            Response response = tcpClient.sendCommand(CommandManager.getCommands().get("show").execute(new String[]{"show", ""}));
            System.out.println("Ticekts");
            System.out.println(response.getData().toString());
            return (List<Ticket>) response.getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
