package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.managers.collections.TicketCollectionManager;

/**
 * Команда 'save'. Сохраняет коллекции в файлы.
 * @author zevtos
 */
public class Save extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды Save.
     *
     * @param ticketCollectionManager менеджер коллекции билетов
     */
    public Save(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.SAVE, "сохранить коллекции в файлы");
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
            ticketCollectionManager.saveCollection();
            return new Response(true, null);
        } catch (Exception e){
            return new Response(false, e.toString());
        }
    }
}
