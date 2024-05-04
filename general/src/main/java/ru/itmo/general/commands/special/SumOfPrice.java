package ru.itmo.general.commands.special;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'sum_of_price'. Сумма цен всех билетов.
 * @author zevtos
 */
public class SumOfPrice extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    public SumOfPrice() {
        super(CommandName.SUM_OF_PRICE, "вывести сумму значений поля price для всех элементов коллекции");
    }
    public SumOfPrice(CollectionManager<Ticket> ticketCollectionManager) {
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
            var sumOfPrice = getSumOfPrice();

            return new Response(true, null, sumOfPrice);

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!", 0);
        }
    }
    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false,
                    getName(),
                    getUsingError());
        }
    }
    private Double getSumOfPrice() {
        return ticketCollectionManager.getCollection().stream()
                .map(Ticket::getPrice)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
