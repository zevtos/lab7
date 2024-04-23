package ru.itmo.server.commands.special;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'sum_of_price'. Сумма цен всех билетов.
 * @author zevtos
 */
public class SumOfPrice extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    public SumOfPrice(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.SUM_OF_PRICE, "вывести сумму значений поля price для всех элементов коллекции");
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

    private Double getSumOfPrice() {
        return ticketCollectionManager.getCollection().stream()
                .map(Ticket::getPrice)
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
