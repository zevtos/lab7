package ru.itmo.client.commands.update;

import ru.itmo.general.commands.Command;
import ru.itmo.client.forms.TicketForm;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.*;
import ru.itmo.general.network.Request;

/**
 * Команда 'update'. Обновляет элемент коллекции.
 * @author zevtos
 */
public class Update extends Command {
    private final Console console;
    

    public Update(Console console) {
        super(CommandName.UPDATE, "{element} обновить значение элемента коллекции по ID");
        this.console = console;
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

            var newTicket = (new TicketForm(console)).build();
            newTicket.setId(id);
            return new Request(getName(), newTicket);
//            if(!resp.isSuccess()) throw new ResponseException(resp.getMessage());
//
//            console.println("Билет успешно обновлен.");
//            return true;

        } catch (InvalidNumberOfElementsException exception) {
            console.printError("Неправильное количество аргументов!");
            console.println(getUsingError());
        }
//        catch (ResponseException exception) {
//            console.printError(exception.getMessage());
//        }
        catch (NumberFormatException exception) {
            console.printError("ID должен быть представлен числом!");
        } catch (InvalidScriptInputException e) {
            console.printError("Некорректный ввод в скрипте!");
        } catch (InvalidFormException e) {
            console.printError("Поля билета не валидны! Билет не обновлен!");
        }
        return new Request();
    }
}
