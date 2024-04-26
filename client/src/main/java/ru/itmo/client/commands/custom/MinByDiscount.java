package ru.itmo.client.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;

/**
 * Команда 'min_by_discount'. выводит элемент с минимальным discount.
 * @author zevtos
 */
public class MinByDiscount extends Command {
    private final Console console;
    

    public MinByDiscount(Console console) {
        super(CommandName.MIN_BY_DISCOUNT, "вывести любой объект из коллекции, значение поля discount которого является минимальным");
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
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            return new Request();
//            if(resp.isSuccess()){
//                console.println(resp.getTicketInfo());
//            }else throw new ResponseException(resp.getMessage());
//            return true;

        } catch (InvalidNumberOfElementsException exception) {
            console.printError(getClass(), "Неправильное количество аргументов!");
            return new Request(false, getName(), getUsingError());
        }
//        catch (ResponseException exception) {
//            console.printError(getClass(), exception.getMessage());
//        }
    }
}
