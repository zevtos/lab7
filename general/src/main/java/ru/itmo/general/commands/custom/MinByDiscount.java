package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Command 'min_by_discount'. Displays the element with the minimum discount.
 *
 * @autor zevtos
 */
public class MinByDiscount extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public MinByDiscount() {
        super(CommandName.MIN_BY_DISCOUNT, "display any object from the collection whose discount field value is the minimum");
    }

    public MinByDiscount(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Executes the command.
     *
     * @param request the command request
     * @return the response indicating the success or failure of the command execution
     */
    @Override
    public Response execute(Request request) {
        try {
            if (ticketCollectionManager.collectionSize() == 0) throw new EmptyValueException();

            Ticket minTicketByDiscount = minByDiscount();
            return new Response(true, null, minTicketByDiscount.toString());
        } catch (EmptyValueException exception) {
            return new Response(false, "The collection is empty!", null);
        }
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments
     * @return the request indicating the success or failure of the command execution
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length > 1 && !arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            return new Request(getName(), null);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }

    private Ticket minByDiscount() {
        long minDiscount = 101;
        int ticketId = -1;
        for (Ticket c : ticketCollectionManager.getCollection()) {
            if (c.getDiscount() != null && c.getDiscount() < minDiscount) {
                minDiscount = c.getDiscount();
                ticketId = c.getId();
            }
        }
        if (ticketId == -1) return ticketCollectionManager.getFirst();
        return ticketCollectionManager.byId(ticketId);
    }
}
