package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.network.Request;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по ID.
 * @author zevtos
 */
public class Remove extends Command {
    private final Console console;


    /**
     * Конструктор для создания экземпляра команды Remove.
     *
     * @param console объект для взаимодействия с консолью
     */
    public Remove(Console console) {
        super(CommandName.REMOVE_BY_ID, "<ID> удалить ticket из коллекции по ID");
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
            if (arguments.length < 2 || arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            int id = Integer.parseInt(arguments[1]);
            return new Request(getName(), id);
//            Response resp;
//            if (!(resp = tcpClient.sendCommand(new RemoveRequest(id))).isSuccess()) {
//                console.printError(resp.getMessage()); throw new ResponseException(resp.getMessage());
//            }
//
//            console.println("Билет успешно удален.");
//            return true;
        } catch (InvalidNumberOfElementsException exception) {
            console.println(getUsingError());
        } catch (NumberFormatException exception) {
            console.printError("ID должен быть представлен числом!");}

//        } catch (ResponseException e) {
//            console.printError(e.getMessage());
//        }

        return new Request();
    }
}
