package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 * @author zevtos
 */
public class Show extends Command {
    private final Console console;


    /**
     * Конструктор для создания экземпляра команды Show.
     *
     * @param console объект для взаимодействия с консолью
     */
    public Show(Console console) {
        super(CommandName.SHOW, "вывести все элементы коллекции Ticket");
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
        if (!arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
