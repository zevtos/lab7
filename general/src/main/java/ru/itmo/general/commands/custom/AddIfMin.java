package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.console.Console;

/**
 * Команда 'add_if_min'. Добавляет новый элемент в коллекцию, если его цена меньше минимальной.
 *
 * @author zevtos
 */
public class AddIfMin extends Command {
    private Form<Ticket> ticketForm;
    private CollectionManager<Ticket> ticketCollectionManager;

    public AddIfMin() {
        super(CommandName.ADD_IF_MIN, "{element} добавить новый элемент в коллекцию, если его цена меньше минимальной цены этой коллекции");
    }

    /**
     * Конструктор для создания экземпляра команды AddIfMin.
     *
     * @param ticketCollectionManager менеджер коллекции
     */
    public AddIfMin(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Конструктор для создания экземпляра команды AddIfMin.
     *
     * @param console объект для взаимодействия с консолью
     */
    public AddIfMin(Form<Ticket> ticketForm) {
        this();
        this.ticketForm = ticketForm;
    }

    /**
     * Выполняет команду
     *
     * @param request запрос на выполнение команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            var ticket = ((Ticket) request.getData());

            var minPrice = minPrice();
            if (ticket.getPrice() < minPrice) {
                ticketCollectionManager.add(ticket, request.getUserId());
                return new Response(true, "Билет успешно добавлен!", minPrice);
            } else {
                return new Response(false, null, minPrice);
            }
        } catch (Exception e) {
            return new Response(false, e.toString(), null);
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
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            var ticket = ticketForm.build();

            return new Request(getName(), ticket);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        } catch (InvalidFormException exception) {
            return new Request(
                    false,
                    getName(),
                    "Поля билета не валидны! Билет не создан!");
        } catch (InvalidScriptInputException ignored) {
            return new Request(false,
                    getName(),
                    "Ошибка чтения из скрипта");
        }
    }

    private Double minPrice() {
        return ticketCollectionManager.getCollection().stream()
                .map(Ticket::getPrice)
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(Double.MAX_VALUE);
    }
}