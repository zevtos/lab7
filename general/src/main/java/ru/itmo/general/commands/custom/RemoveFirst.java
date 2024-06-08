package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.NotFoundException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Accessible;

import java.rmi.AccessException;

/**
 * Command 'remove_first'. Removes the first element from the collection.
 *
 * @autor zevtos
 */
public class RemoveFirst extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    private Accessible dao;

    public RemoveFirst() {
        super(CommandName.REMOVE_FIRST, "remove the first element from the collection");
    }

    public RemoveFirst(CollectionManager<Ticket> ticketCollectionManager, Accessible dao) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
        this.dao = dao;
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

            var ticketToRemove = ticketCollectionManager.getFirst();
            if (ticketToRemove == null) throw new NotFoundException();
            if (!dao.checkOwnership(ticketToRemove.getId(), request.getUserId()))
                throw new AccessException("You do not have access to this ticket");
            ticketCollectionManager.remove(ticketToRemove);
            return new Response(true, "Ticket successfully removed.");
        } catch (EmptyValueException exception) {
            return new Response(false, "The collection is empty!");
        } catch (NotFoundException exception) {
            return new Response(false, "No ticket with such ID exists in the collection!");
        } catch (AccessException exception) {
            return new Response(false, exception.getMessage());
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
}
