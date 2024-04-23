package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

import java.time.LocalDateTime;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 *
 * @author zevtos
 */
public class Info extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды Info.
     *
     * @param ticketCollectionManager менеджер коллекции билетов
     */
    public Info(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.INFO, "вывести информацию о коллекции");
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
        String ticketLastSaveTimeString = (ticketLastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                ticketLastSaveTime.toLocalDate().toString() + " " + ticketLastSaveTime.toLocalTime().toString();
        LocalDateTime personLastSaveTime = ticketCollectionManager.getPersonManager().getLastSaveTime();
        String personLastSaveTimeString = (personLastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                personLastSaveTime.toLocalDate().toString() + " " + personLastSaveTime.toLocalTime().toString();

        String message;

        message = "Сведения о коллекции:" +'\n' +
                " Тип: " + ticketCollectionManager.collectionType() + '\n' +
                " Количество элементов Ticket: " + ticketCollectionManager.collectionSize() +'\n' +
                " Количество элементов Person: " + ticketCollectionManager.getPersonManager().collectionSize() +'\n' +
                " Дата последнего сохранения:" +'\n' +
                "\tTickets: " + ticketLastSaveTimeString +'\n' +
                "\tPersons: " + personLastSaveTimeString;

        return new Response(true, null, message);
    }
}
