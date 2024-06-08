package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.util.List;

/**
 * Command 'show'. Displays all elements in the collection.
 *
 * @autor zevtos
 */
public class Show extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public Show() {
        super(CommandName.SHOW, "display all elements in the Ticket collection");
    }

    /**
     * Constructor for creating an instance of the Show command.
     *
     * @param ticketCollectionManager the collection manager
     */
    public Show(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments
     * @return the response indicating the success or failure of the command execution
     */
    @Override
    public Response execute(Request arguments) {
        List<Ticket> tickets = ticketCollectionManager.getCollection();
        return new Response(true, "Collection fetched successfully", tickets);
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments
     * @return the request indicating the success or failure of the command execution
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
