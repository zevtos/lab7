package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Command 'clear'. Clears the collection.
 *
 * @author zevtos
 */
public class Clear extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public Clear() {
        super(CommandName.CLEAR, "clear the collection");
    }

    public Clear(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Executes the command.
     *
     * @return the success of the command execution.
     */
    @Override
    public Response execute(Request request) {
        try {
            int userId = request.getUserId();
            ticketCollectionManager.clear(userId);
            return new Response(true, "The collection has been cleared of the current user's tickets.");
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
    }

    /**
     * Executes the command.
     *
     * @return the success of the command execution.
     */
    @Override
    public Request execute(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
