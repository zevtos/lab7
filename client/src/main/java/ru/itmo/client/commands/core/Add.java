package ru.itmo.client.commands.core;


import ru.itmo.general.commands.Command;
import ru.itmo.client.forms.TicketForm;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 *
 * @author zevtos
 */
public class Add extends Command {
    private final Console console;

    /**
     * Конструктор для создания экземпляра команды Add.
     *
     * @param console                 объект для взаимодействия с консолью
     */
    public Add(Console console) {
        super(CommandName.ADD, "{element} добавить новый объект Ticket в коллекцию");
        this.console = console;
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

            var newTicket = (new TicketForm(console)).build();
            return new Request(getName(), newTicket);
//            var response = (AddResponse) tcpClient.sendCommand(new AddRequest(newTicket));
//            if (response.getMessage() != null && !response.getMessage().isEmpty()) {
//                throw new RuntimeException("Ошибка при выполнении запроса: " + response.getMessage());
//            }
//
//            console.println("Новый продукт с id=" + response.getNewId() + " успешно добавлен!");
//            return true;

        } catch (InvalidNumberOfElementsException exception) {
            console.printError(getClass(), "Неправильное количество аргументов!");
            console.println(getUsingError());
        } catch (InvalidFormException exception) {
            console.printError(getClass(), "Поля билета не валидны! Билет не создан!");
        } catch (InvalidScriptInputException ignored) {
            // Ignored
        }
        return new Request();
    }
}
