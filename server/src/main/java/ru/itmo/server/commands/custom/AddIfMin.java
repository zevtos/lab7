package ru.itmo.server.commands.custom;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'add_if_min'. Добавляет новый элемент в коллекцию, если его цена меньше минимальной.
 *
 * @author zevtos
 */
public class AddIfMin extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды AddIfMin.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public AddIfMin(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.ADD_IF_MIN, "{element} добавить новый элемент в коллекцию, если его цена меньше минимальной цены этой коллекции");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            var ticket = ((Ticket) request.getData());

            var minPrice = minPrice();
            if (ticket.getPrice() < minPrice) {
                ticketCollectionManager.add(ticket);
                return new Response(true, "Билет успешно добавлен!", minPrice);
            } else {
                return new Response(false, null, minPrice);
            }
        }catch (Exception e){
            return new Response(false, e.toString(), null);
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