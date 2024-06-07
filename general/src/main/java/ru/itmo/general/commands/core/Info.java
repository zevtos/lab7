package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.time.LocalDateTime;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;

    public Info() {
        super(CommandName.INFO, "вывести информацию о коллекции");
    }

    /**
     * Конструктор для создания экземпляра команды Info.
     *
     * @param ticketCollectionManager менеджер коллекции билетов
     */
    public Info(CollectionManager<Ticket> ticketCollectionManager) {
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
        LocalDateTime ticketLastSaveTime = ticketCollectionManager.getLastSaveTime();
        String ticketLastSaveTimeString = (ticketLastSaveTime == null) ? null :
                ticketLastSaveTime.toLocalDate().toString() + " " + ticketLastSaveTime.toLocalTime().toString();

        Object[] responseData = new Object[]{
                ticketCollectionManager.collectionType(),
                ticketCollectionManager.collectionSize(),
                ticketLastSaveTimeString
        };

        return new Response(true, null, responseData);
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), null);
    }
}
