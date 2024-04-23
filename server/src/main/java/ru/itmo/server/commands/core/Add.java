package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.managers.collections.TicketCollectionManager;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author zevtos
 */
public class Add extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды Add.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public Add(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.ADD, "{element} добавить новый объект Ticket в коллекцию");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду.
     *
     * @param request
     * @return Успешность выполнения команды
     */
    @Override
    public Response execute(Request request) {
        try {
            var ticket = ((Ticket)request.getData());
            ticket.setId(ticketCollectionManager.getFreeId());
            ticket.getPerson().setId(ticketCollectionManager.getPersonManager().getFreeId());
            if (!ticket.validate()) {
                return new Response(false, "Билет не добавлен, поля билета не валидны!");
            }
            ticket.setId(ticketCollectionManager.getFreeId());
            if(!ticketCollectionManager.add(ticket)) return new Response(false, "Билет уже существует", -1);
            return new Response(true, null, ticketCollectionManager.getFreeId());
        } catch (Exception e) {
            return new Response(false, e.toString(), -1);
        }
    }
}
