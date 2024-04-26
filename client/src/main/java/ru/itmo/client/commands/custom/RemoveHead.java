package ru.itmo.client.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;

/**
 * Команда 'remove_head'. Выводит первый элемент коллекции и удаляет его.
 * @author zevtos
 */
public class RemoveHead extends Command {
    private final Console console;
    

    public RemoveHead(Console console) {
        super(CommandName.REMOVE_HEAD, "вывести первый элемент коллекции и удалить его");
        this.console = console;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
//            if(resp.isSuccess()) {
//                console.println("Билет успешно удален.");
//            }else throw new ResponseException(resp.getMessage());
//            return true;

        } catch (InvalidNumberOfElementsException exception) {
            console.printError(getClass(), "Неправильное количество аргументов!");
            return new Request(false, getName(), getUsingError());
        }
//        catch (ResponseException e){
//            console.printError(getClass(), e.getMessage());
//        }
    }
}
