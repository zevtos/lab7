package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'min_by_discount'. выводит элемент с минимальным discount.
 * @author zevtos
 */
public class MinByDiscount extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    public MinByDiscount() {
        super(CommandName.MIN_BY_DISCOUNT, "вывести любой объект из коллекции, значение поля discount которого является минимальным");

    }
    public MinByDiscount(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            if (ticketCollectionManager.collectionSize() == 0) throw new EmptyValueException();

            Ticket minTicketByDiscount = minByDiscount();
            return new Response(true, null, minTicketByDiscount.toString());

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!", null);
        }
    }
    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            return new Request(getName(), null);

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }

    private Ticket minByDiscount(){
        long minDiscount = 101;
        int ticketId = -1;
        for (Ticket c : ticketCollectionManager.getCollection()) {
            if (c.getDiscount() != null && c.getDiscount() < minDiscount) {
                minDiscount = c.getDiscount();
                ticketId = c.getId();
            }
        }
        if(ticketId == -1) return ticketCollectionManager.getFirst();
        return ticketCollectionManager.byId(ticketId);
    }
}
