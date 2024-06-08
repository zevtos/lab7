package ru.itmo.general.commands.core;

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
 * Command 'remove_by_id'. Removes an element from the collection by ID.
 *
 * @autor zevtos
 */
public class Remove extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    private Accessible dao;

    public Remove() {
        super(CommandName.REMOVE_BY_ID, "<ID> remove a ticket from the collection by ID");
    }

    /**
     * Constructor for creating an instance of the Remove command.
     *
     * @param ticketCollectionManager the ticket collection manager
     * @param dao the data access object
     */
    public Remove(CollectionManager<Ticket> ticketCollectionManager, Accessible dao) {
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

            var id = (Integer) request.getData();
            if (!dao.checkOwnership(id, request.getUserId()))
                throw new AccessException("You do not have access to this ticket");
            if (!ticketCollectionManager.remove(id)) throw new NotFoundException();

            return new Response(true, "Ticket successfully removed.");
        } catch (EmptyValueException exception) {
            return new Response(false, "The collection is empty!");
        } catch (NotFoundException exception) {
            return new Response(false, "No ticket with the given ID exists in the collection or it was not created by you!");
        } catch (AccessException e) {
            return new Response(false, e.getMessage());
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
            if (arguments.length < 2 || arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            int id = Integer.parseInt(arguments[1]);
            return new Request(getName(), id);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (NumberFormatException exception) {
            return new Request(false, getName(), "ID must be a number!");
        }
    }
}
