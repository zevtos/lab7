package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Command 'execute_script'. Execute a script from a file.
 *
 * @autor zevtos
 */
public class ExecuteScript extends Command {
    public ExecuteScript() {
        super(CommandName.EXECUTE_SCRIPT, "<file_name> execute a script from the specified file");
    }

    /**
     * Executes the command.
     *
     * @return the success of the command execution.
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), null);
    }
}
