package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.server.managers.collections.TicketCollectionManager;

/**
 * Команда 'exit'. Завершает выполнение программы (без сохранения в файл).
 * @author zevtos
 */
public class Exit extends Command {
    private TicketCollectionManager ticketCollectionManager;
    /**
     * Конструктор для создания экземпляра команды Exit.
     *
     */
    public Exit(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.EXIT, "завершить программу (без сохранения в файл)");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды (ожидается отсутствие аргументов)
     * @return Успешность выполнения команды
     */
    @Override
    public Response execute(Request arguments) {
        try {
            ticketCollectionManager.saveCollection();
            return new Response(true, null);
        } catch (Exception e){
            return new Response(false, e.toString());
        }
    }
}
