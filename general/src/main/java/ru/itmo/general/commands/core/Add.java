package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Command 'add'. Adds a new element to the collection.
 *
 * @autor zevtos
 */
public class Add extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    private Form<Ticket> ticketForm;

    public Add() {
        super(CommandName.ADD, "{element} add a new Ticket object to the collection");
    }

    /**
     * Constructor to create an instance of the Add command.
     *
     * @param ticketCollectionManager the collection manager
     */
    public Add(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    public Add(Form<Ticket> ticketForm) {
        this();
        this.ticketForm = ticketForm;
    }

    /**
     * Executes the command.
     *
     * @param request the request to add the ticket
     * @return the success of the command execution
     */
    @Override
    public Response execute(Request request) {
        try {
            var ticket = ((Ticket) request.getData());
            if (!ticket.validate()) {
                return new Response(false, "Ticket not added, ticket fields are not valid!");
            }
            ticket.setUserId(request.getUserId());
            Integer newID = ticketCollectionManager.add(ticket, request.getUserId());
            if (newID == -1)
                return new Response(false, "Ticket already exists", -1);
            return new Response(true, "Ticket successfully added", newID);
        } catch (Exception e) {
            return new Response(false, e.toString(), -1);
        }
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments (expected to be empty)
     * @return the success of the command execution
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            var newTicket = ticketForm.build();
            return new Request(getName(), newTicket);

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (InvalidFormException exception) {
            return new Request(false, getName(), "Ticket fields are not valid! Ticket not created!");
        } catch (InvalidScriptInputException ignored) {
            return new Request(false, getName(), "Script reading error");
        }
    }
}
