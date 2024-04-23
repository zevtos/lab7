package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 * @author zevtos
 */
public class Show extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды Show.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public Show(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.SHOW, "вывести все элементы коллекции Ticket");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request arguments) {

        String message = ticketCollectionManager.toString();
        return new Response(true, null, message);
    }
}
