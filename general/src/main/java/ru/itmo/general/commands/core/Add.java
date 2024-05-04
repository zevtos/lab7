package ru.itmo.general.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.utility.console.Console;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author zevtos
 */
public class Add extends Command {
    private Console console;
    private CollectionManager<Ticket> ticketCollectionManager;
    private Form<Ticket> ticketForm;

    public Add(){
        super(CommandName.ADD, "{element} добавить новый объект Ticket в коллекцию");
    }
    /**
     * Конструктор для создания экземпляра команды Add.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public Add(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }
    public Add(Console console, Form<Ticket> ticketForm) {
        this();
        this.console = console;
        this.ticketForm = ticketForm;
    }

    /**
     * Выполняет команду.
     *
     * @param request запрос на добавление билета
     * @return Успешность выполнения команды
     */
    @Override
    public Response execute(Request request) {
        try {
            var ticket = ((Ticket)request.getData());
            ticket.setId(ticketCollectionManager.getFreeId());
            if (!ticket.validate()) {
                return new Response(false, "Билет не добавлен, поля билета не валидны!");
            }
            ticket.setId(ticketCollectionManager.getFreeId());
            if(!ticketCollectionManager.add(ticket)) return new Response(false, "Билет уже существует", -1);
            return new Response(true, null, ticketCollectionManager.getFreeId());
        } catch (Exception e) {
            return new Response(false, e.toString(), -1);
        }
    }
    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды (ожидается отсутствие аргументов)
     * @return Успешность выполнения команды
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            console.println("* Создание нового продукта:");

            var newTicket = ticketForm.build();
            return new Request(getName(), newTicket);

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (InvalidFormException exception) {
            return new Request(false, getName(), "Поля билета не валидны! Билет не создан!");
        } catch (InvalidScriptInputException ignored) {
            return new Request(false, getName(), "Ошибка чтения из скрипта");
        }
    }
}
