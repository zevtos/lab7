package ru.itmo.client.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;

/**
 * Команда 'max_by_name'. Выводит элемент с максимальным именем.
 *
 * @author zevtos
 */
public class MaxByName extends Command {
    private final Console console;


    public MaxByName(Console console) {
        super(CommandName.MAX_BY_NAME, "вывести любой объект из коллекции, значение поля name которого является максимальным");
        this.console = console;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length > 1 && !arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
//            if(resp.isSuccess()) {
//                console.println(resp.getTicketInfo());
//            }else{
//                throw new ResponseException(resp.getMessage());
//            }
//            return true;

        } catch (InvalidNumberOfElementsException exception) {
            console.printError(getClass(), "Неправильное количество аргументов!");
            console.println(getUsingError());
        }
//        } catch (ResponseException e) {
//            console.printError(getClass(), e.getMessage());;
//        }
        return new Request();
    }
}
