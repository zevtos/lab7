package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.time.LocalDateTime;

/**
 * Command 'info'. Displays information about the collection.
 *
 * @autor zevtos
 */
public class Info extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public Info() {
        super(CommandName.INFO, "display information about the collection");
    }

    /**
     * Constructor to create an instance of the Info command.
     *
     * @param ticketCollectionManager the ticket collection manager
     */
    public Info(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments
     * @return the success of the command execution.
     */
    @Override
    public Response execute(Request arguments) {
        LocalDateTime ticketLastSaveTime = ticketCollectionManager.getLastSaveTime();
        String ticketLastSaveTimeString = (ticketLastSaveTime == null) ? null :
                ticketLastSaveTime.toLocalDate().toString() + " " + ticketLastSaveTime.toLocalTime().toString();

        Object[] responseData = new Object[]{
                ticketCollectionManager.collectionType(),
                ticketCollectionManager.collectionSize(),
                ticketLastSaveTimeString
        };

        return new Response(true, null, responseData);
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments
     * @return the success of the command execution.
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), null);
    }
}
