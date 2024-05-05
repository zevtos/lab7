package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.utility.base.Accessible;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.commands.Command;

import java.rmi.AccessException;

/**
 * Команда 'remove_head'. Выводит первый элемент коллекции и удаляет его.
 *
 * @author zevtos
 */
public class RemoveHead extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    private Accessible dao;

    public RemoveHead() {
        super(CommandName.REMOVE_HEAD, "вывести первый элемент коллекции и удалить его");
    }

    public RemoveHead(CollectionManager<Ticket> ticketCollectionManager, Accessible dao) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
        this.dao = dao;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            if (ticketCollectionManager.collectionSize() == 0) throw new EmptyValueException();
            Ticket ticketToRemove = ticketCollectionManager.getLast();
            if (!dao.checkOwnership(ticketToRemove.getId(), request.getUserId()))
                throw new AccessException("У вас нет доступа к этому билету");
            ticketCollectionManager.remove(ticketToRemove);
            return new Response(true, "Билет успешно удален.");

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (AccessException e) {
            return new Response(false, e.getMessage());
        }
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
