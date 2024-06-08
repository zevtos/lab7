package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Command 'history'. Displays the history of used commands.
 *
 * @autor zevtos
 */
public class History extends Command {

    /**
     * Constructor for creating an instance of the History command.
     */
    public History() {
        super(CommandName.HISTORY, "display the list of used commands");
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments (expected to be empty in this case)
     * @return the success of the command execution
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
