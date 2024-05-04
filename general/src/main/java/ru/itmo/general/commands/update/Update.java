package ru.itmo.general.commands.update;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.*;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.commands.Command;
import ru.itmo.general.utility.console.Console;

/**
 * Команда 'update'. Обновляет элемент коллекции.
 * @author zevtos
 */
public class Update extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    private Console console;
    private Form<Ticket> ticketForm;
    public Update(){
        super(CommandName.UPDATE, "<ID> {element} обновить значение элемента коллекции по ID");
    }
    public Update(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }
    public Update(Console console, Form<Ticket> ticketForm){
        this();
        this.console = console;
        this.ticketForm = ticketForm;
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

            var new_ticket = ((Ticket) request.getData());
            var id = new_ticket.getId();
            var ticket = ticketCollectionManager.byId(id);
            if (ticket == null) throw new NotFoundException();

            ticket.update(new_ticket);

            return new Response(true, "Билет успешно обновлен.");

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (NotFoundException exception) {
            return new Response(false, "Билета с таким ID в коллекции нет!");
        }
    }
    /**
     * Выполняет команду
     *
     * @param arguments Аргументы команды.
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            var id = Integer.parseInt(arguments[1]);

            console.println("* Введите данные обновленного билета:");
            console.prompt();

            var newTicket = ticketForm.build();
            newTicket.setId(id);
            return new Request(getName(), newTicket);

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false,
                    getName(),
                    getUsingError());
        } catch (NumberFormatException exception) {
            return new Request(false,
                    getName(),
                    "ID должен быть представлен числом!");
        } catch (InvalidScriptInputException e) {
            return new Request(false,
                    getName(),
                    "Некорректный ввод в скрипте!");
        } catch (InvalidFormException e) {
            return new Request(false,
                    getName(),
                    "Поля билета не валидны! Билет не обновлен!");
        }
    }
}
