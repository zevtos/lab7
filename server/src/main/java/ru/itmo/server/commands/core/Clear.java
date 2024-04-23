package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.managers.collections.TicketCollectionManager;

/**
 * Команда 'clear'. Очищает коллекцию.
 *
 * @author zevtos
 */
public class Clear extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    public Clear(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.CLEAR, "очистить коллекцию");
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
            ticketCollectionManager.clearCollection();
            return new Response(true, "Коллекция очищена!");
        } catch (Exception e){
            return new Response(false, e.toString());
        }
    }
}
