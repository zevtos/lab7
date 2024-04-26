package ru.itmo.client.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.client.forms.TicketForm;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.network.Request;


/**
 * Команда 'add_if_min'. Добавляет новый элемент в коллекцию, если его цена меньше минимальной.
 *
 * @author zevtos
 */
public class AddIfMin extends Command {
    private final Console console;
    

    /**
     * Конструктор для создания экземпляра команды AddIfMin.
     *
     * @param console           объект для взаимодействия с консолью
     */
    public AddIfMin(Console console) {
        super(CommandName.ADD_IF_MIN, "{element} добавить новый элемент в коллекцию, если его цена меньше минимальной цены этой коллекции");
        this.console = console;
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
            console.println("* Создание нового билета (add_if_min):");
            var ticket = (new TicketForm(console)).build();

            return new Request(getName(), ticket);

//            if (resp.isSuccess()) {
//                console.println("Билет успешно добавлен!");
//            } else {
//                var minPrice = resp.getMinPrice();
//                console.println("Билет не добавлен, цена не минимальная (" + ticket.getPrice() + " > " + minPrice +")");
//            }
//            return true;

        } catch (InvalidNumberOfElementsException exception) {
            console.printError(getClass(), "Неправильное количество аргументов!");
            console.println(getUsingError());
        } catch (InvalidFormException exception) {
            console.printError(getClass(), "Поля билета не валидны! Продукт не создан!");
        } catch (InvalidScriptInputException ignored) {}
        return new Request();
    }

}