package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.util.List;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 *
 * @author zevtos
 */
public class Show extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public Show() {
        super(CommandName.SHOW, "вывести все элементы коллекции Ticket");
    }

    /**
     * Конструктор для создания экземпляра команды Show.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public Show(CollectionManager<Ticket> ticketCollectionManager) {
        this();
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
        List<Ticket> tickets = ticketCollectionManager.getCollection();
        return new Response(true, "Collection fetched successfully", tickets);
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
