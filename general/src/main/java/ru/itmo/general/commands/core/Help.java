package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;

/**
 * Command 'help'. Displays help for available commands.
 *
 * @autor zevtos
 */
public class Help extends Command {

    /**
     * Constructor to create an instance of the Help command.
     */
    public Help() {
        super(CommandName.HELP, "display help for available commands");
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments (expected to be empty)
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
