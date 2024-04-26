package ru.itmo.server.commands.update;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.NotFoundException;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'update'. Обновляет элемент коллекции.
 * @author zevtos
 */
public class Update extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    public Update(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.UPDATE, "<ID> {element} обновить значение элемента коллекции по ID");
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

            var new_ticket = ((Ticket) request.getData());
            var id = new_ticket.getId();
            var ticket = ticketCollectionManager.byId(id);
            if (ticket == null) throw new NotFoundException();

            ticket.update(new_ticket);

            return new Response(true, "Билет успешно обновлен.");

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (NotFoundException exception) {
            return new Response(false, "Билета с таким ID в коллекции нет!");
        }
    }
}
