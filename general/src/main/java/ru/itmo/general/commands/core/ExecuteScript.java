package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Команда 'execute_script'. Выполнить скрипт из файла.
 *
 * @author zevtos
 */
public class ExecuteScript extends Command {
    public ExecuteScript() {
        super(CommandName.EXECUTE_SCRIPT, "<file_name> исполнить скрипт из указанного файла");
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

        return new Request(getName(), null);
    }
}
