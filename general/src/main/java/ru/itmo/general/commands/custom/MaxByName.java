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
 * Команда 'max_by_name'. Выводит элемент с максимальным именем.
 *
 * @author zevtos
 */
public class MaxByName extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    public MaxByName(){
        super(CommandName.MAX_BY_NAME, "вывести любой объект из коллекции, значение поля name которого является максимальным");

    }
    public MaxByName(CollectionManager<Ticket> ticketCollectionManager) {
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
            Ticket ticket = maxByName();
            return new Response(true, null, ticket.toString());

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
            if (arguments.length > 1 && !arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false,
                    getName(),
                    getUsingError());
        }
    }

    private Ticket maxByName() {
        String maxName = "";
        int ticketId = -1;
        for (Ticket c : ticketCollectionManager.getCollection()) {
            if (c.getName().compareTo(maxName) < 0) {
                maxName = c.getName();
                ticketId = c.getId();
            }
        }
        if (ticketId == -1) return ticketCollectionManager.getFirst();
        return ticketCollectionManager.byId(ticketId);
    }
}
