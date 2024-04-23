package ru.itmo.client.commands.special;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;

/**
 * Команда 'sum_of_price'. Сумма цен всех билетов.
 * @author zevtos
 */
public class SumOfPrice extends Command {
    private final Console console;
    

    public SumOfPrice(Console console) {
        super(CommandName.SUM_OF_PRICE, "вывести сумму значений поля price для всех элементов коллекции");
        this.console = console;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
//            if(resp.isSuccess()) {
//                var sumOfPrice = resp.getSum();
//                console.println("Сумма цен всех билетов: " + sumOfPrice);
//            }else throw new ResponseException(resp.getMessage());
        } catch (InvalidNumberOfElementsException exception) {
            console.println(getUsingError());
        }
//        catch (ResponseException exception) {
//            console.println(exception.getMessage());
//        }
        return new Request();
    }
}
