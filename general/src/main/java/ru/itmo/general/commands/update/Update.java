package ru.itmo.general.commands.update;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.*;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Accessible;

import java.rmi.AccessException;

/**
 * Command 'update'. Updates an element in the collection.
 *
 * @autor zevtos
 */
public class Update extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    private Form<Ticket> ticketForm;
    private Accessible dao;

    public Update() {
        super(CommandName.UPDATE, "<ID> {element} update the value of the collection element by ID");
    }

    public Update(CollectionManager<Ticket> ticketCollectionManager, Accessible dao) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
        this.dao = dao;
    }

    public Update(Form<Ticket> ticketForm) {
        this();
        this.ticketForm = ticketForm;
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

            var newTicket = (Ticket) request.getData();
            var id = newTicket.getId();
            var ticket = ticketCollectionManager.byId(id);
            if (ticket == null) throw new NotFoundException();
            if (!dao.checkOwnership(ticket.getId(), request.getUserId()))
                throw new AccessException("You do not have access to this ticket");
            ticket.update(newTicket);

            return new Response(true, "Ticket successfully updated.");
        } catch (EmptyValueException exception) {
            return new Response(false, "The collection is empty!");
        } catch (NotFoundException exception) {
            return new Response(false, "No ticket with such ID in the collection!");
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
            if (arguments.length <= 1 || arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            var id = Integer.parseInt(arguments[1]);

            var newTicket = ticketForm.build();
            newTicket.setId(id);
            return new Request(getName(), newTicket);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (NumberFormatException exception) {
            return new Request(false, getName(), "ID must be a number!");
        } catch (InvalidScriptInputException e) {
            return new Request(false, getName(), "Invalid input in the script!");
        } catch (InvalidFormException e) {
            return new Request(false, getName(), "Ticket fields are not valid! Ticket not updated!");
        }
    }
}
