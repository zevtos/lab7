package ru.itmo.server.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.NotFoundException;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.server.managers.collections.TicketCollectionManager;
import ru.itmo.general.commands.Command;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по ID.
 * @author zevtos
 */
public class Remove extends Command {
    private final TicketCollectionManager ticketCollectionManager;

    /**
     * Конструктор для создания экземпляра команды Remove.
     *
     * @param ticketCollectionManager менеджер коллекции билетов
     */
    public Remove(TicketCollectionManager ticketCollectionManager) {
        super(CommandName.REMOVE_BY_ID, "<ID> удалить ticket из коллекции по ID");
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {

            if (ticketCollectionManager.collectionSize() == 0) throw new EmptyValueException();

            var id = ((Integer) request.getData());
            if(!ticketCollectionManager.remove(id)) throw new NotFoundException();

            return new Response(true, "Билет успешно удален.");

        }catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (NotFoundException exception) {
            return new Response(false, "Билета с таким ID в коллекции нет!");
        }
    }
}
