package ru.itmo.general.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.commands.Command;
import ru.itmo.general.utility.console.Console;

/**
 * Команда 'show'. Выводит все элементы коллекции.
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

        String message = ticketCollectionManager.toString();
        return new Response(true, null, message);
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
