package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.NotFoundException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Accessible;
import ru.itmo.general.utility.console.Console;

import java.rmi.AccessException;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по ID.
 *
 * @author zevtos
 */
public class Remove extends Command {
    private Console console;
    private CollectionManager<Ticket> ticketCollectionManager;
    private Accessible dao;

    public Remove() {
        super(CommandName.REMOVE_BY_ID, "<ID> удалить ticket из коллекции по ID");
    }

    /**
     * Конструктор для создания экземпляра команды Remove.
     *
     * @param ticketCollectionManager менеджер коллекции билетов
     */
    public Remove(CollectionManager<Ticket> ticketCollectionManager, Accessible dao) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
        this.dao = dao;
    }

    public Remove(Accessible dao) {
        this();
        this.dao = dao;
    }

    /**
     * Выполняет команду
     *
     * @param request аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {

            if (ticketCollectionManager.collectionSize() == 0) throw new EmptyValueException();

            var id = ((Integer) request.getData());
            if (!dao.checkOwnership(id, request.getUserId()))
                throw new AccessException("У вас нет доступа к этому билету");
            if (!ticketCollectionManager.remove(id)) throw new NotFoundException();

            return new Response(true, "Билет успешно удален.");

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (NotFoundException exception) {
            return new Response(false, "Билета с таким ID в коллекции не существует или он был создан не вами!");
        } catch (AccessException e) {
            return new Response(false, e.getMessage());
        }
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length < 2 || arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            int id = Integer.parseInt(arguments[1]);
            return new Request(getName(), id);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (NumberFormatException exception) {
            return new Request(false, getName(), "ID должен быть представлен числом!");
        }
    }
}
