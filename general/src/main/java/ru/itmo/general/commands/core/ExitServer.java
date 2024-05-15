package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Команда 'exit'. Завершает выполнение программы (сохраняя в файл).
 *
 * @author zevtos
 */
public class ExitServer extends Command {
    private CollectionManager ticketCollectionManager;

    public ExitServer() {
        super(CommandName.EXIT_SERVER, "завершить программу (с сохранением в файл)");
    }

    /**
     * Конструктор для создания экземпляра команды Exit.
     */
    public ExitServer(CollectionManager<Ticket> ticketCollectionManager) {
        this();
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
        return new Response(true, null);
    }
}
