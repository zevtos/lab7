package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.network.Request;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 * @author zevtos
 */
public class Info extends Command {
    private final Console console;
    

    /**
     * Конструктор для создания экземпляра команды Info.
     *
     * @param console объект для взаимодействия с консолью
     */
    public Info(Console console) {
        super(CommandName.INFO, "вывести информацию о коллекции");
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
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), null);
    }
}
