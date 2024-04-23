package ru.itmo.server.commands.custom;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'remove_head'. Выводит первый элемент коллекции и удаляет его.
 * @author zevtos
 */
public class RemoveHead extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    public RemoveHead(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.REMOVE_HEAD, "вывести первый элемент коллекции и удалить его");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request arguments) {
        try {
            if (ticketCollectionManager.collectionSize() == 0) throw new EmptyValueException();
            Ticket ticket = ticketCollectionManager.getFirst();

            var flag = ticketCollectionManager.remove(ticket);
            return new Response(true, "Билет успешно удален.");

        }catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        }
    }
}
