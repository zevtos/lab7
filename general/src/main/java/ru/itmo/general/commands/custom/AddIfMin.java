package ru.itmo.general.commands.custom;

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
 * Command 'add_if_min'. Adds a new element to the collection if its price is less than the minimum price in the collection.
 *
 * @autor zevtos
 */
public class AddIfMin extends Command {
    private Form<Ticket> ticketForm;
    private CollectionManager<Ticket> ticketCollectionManager;

    public AddIfMin() {
        super(CommandName.ADD_IF_MIN, "{element} add a new element to the collection if its price is less than the minimum price in the collection");
    }

    /**
     * Constructor for creating an instance of the AddIfMin command.
     *
     * @param ticketCollectionManager the collection manager
     */
    public AddIfMin(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Constructor for creating an instance of the AddIfMin command.
     *
     * @param ticketForm the form for creating tickets
     */
    public AddIfMin(Form<Ticket> ticketForm) {
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
            var ticket = ((Ticket) request.getData());

            var minPrice = minPrice();
            if (ticket.getPrice() < minPrice) {
                ticketCollectionManager.add(ticket, request.getUserId());
                return new Response(true, "Ticket successfully added!", minPrice);
            } else {
                return new Response(false, "Ticket price is not less than the minimum price in the collection", minPrice);
            }
        } catch (Exception e) {
            return new Response(false, e.toString(), null);
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
            var ticket = ticketForm.build();

            return new Request(getName(), ticket);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (InvalidFormException exception) {
            return new Request(false, getName(), "Ticket fields are not valid! Ticket not created!");
        } catch (InvalidScriptInputException ignored) {
            return new Request(false, getName(), "Script reading error");
        }
    }

    private Double minPrice() {
        return ticketCollectionManager.getCollection().stream()
                .map(Ticket::getPrice)
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(Double.MAX_VALUE);
    }
}
