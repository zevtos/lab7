package ru.itmo.client.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.client.utility.console.Console;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Команда 'execute_script'. Выполнить скрипт из файла.
 *
 * @author zevtos
 */
public class ExecuteScript extends Command {
    private final Console console;

    public ExecuteScript(Console console) {
        super(CommandName.EXECUTE_SCRIPT, "<file_name> исполнить скрипт из указанного файла");
        this.console = console;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        console.println("Выполнение скрипта '" + arguments[1] + "'...");
        return new Request(true, getName(),
                arguments[1]);
    }
}
