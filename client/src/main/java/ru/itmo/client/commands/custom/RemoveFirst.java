package ru.itmo.client.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;

/**
 * Команда 'remove_first'. Удаляет первый элемент из коллекции.
 * @author zevtos
 */
public class RemoveFirst extends Command {
    private final Console console;
    

    public RemoveFirst(Console console) {
        super(CommandName.REMOVE_FIRST, "удалить первый элемент из коллекции");
        this.console = console;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length > 1 && !arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
//            if(resp.isSuccess()) {
//                console.println("Билет успешно удален.");
//            }else throw new ResponseException(resp.getMessage());
//            return true;

        }
//        catch (ResponseException exception) {
//            console.logError(getClass(), exception.getMessage());
//        }
        catch (InvalidNumberOfElementsException exception) {
            console.logError(getClass(), "Неправильное количество аргументов!");
            return new Request(false, getName(), getUsingError());
        }
    }
}
