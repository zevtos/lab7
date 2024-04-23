package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.network.Request;
import ru.itmo.general.commands.CommandName;

/**
 * Команда 'clear'. Очищает коллекцию.
 * @author zevtos
 */
public class Clear extends Command {
    private final Console console;


    public Clear(Console console) {
        super(CommandName.CLEAR, "очистить коллекцию");
        this.console = console;
    }

    /**
     * Выполняет команду
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
