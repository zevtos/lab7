package ru.itmo.general.commands.special;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Command 'sum_of_price'. Sum of prices of all tickets.
 *
 * @autor zevtos
 */
public class SumOfPrice extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public SumOfPrice() {
        super(CommandName.SUM_OF_PRICE, "display the sum of the price field for all elements in the collection");
    }

    public SumOfPrice(CollectionManager<Ticket> ticketCollectionManager) {
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
            var sumOfPrice = getSumOfPrice();

            return new Response(true, null, sumOfPrice);
        } catch (EmptyValueException exception) {
            return new Response(false, "The collection is empty!", 0);
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

    private Double getSumOfPrice() {
        return ticketCollectionManager.getCollection().stream()
                .map(Ticket::getPrice)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
