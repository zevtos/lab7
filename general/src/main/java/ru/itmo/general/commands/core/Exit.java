package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.console.Console;

/**
 * Command 'exit'. Terminates the program (without saving to a file).
 *
 * @autor zevtos
 */
public class Exit extends Command {

    /**
     * Constructor to create an instance of the Exit command.
     */
    public Exit() {
        super(CommandName.EXIT, "terminate the application");
    }

    /**
     * Executes the command.
     *
     * @param arguments the command arguments (expected to be empty)
     * @return the success of the command execution
     */
    @Override
    public Response execute(Request arguments) {
        try {
            return new Response(true, null);
        } catch (Exception e) {
            return new Response(false, e.toString());
        }
    }

    /**
     * Executes the command.
     *
     * @return the success of the command execution.
     */
    @Override
    public Request execute(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), "Terminating the application...");
    }
}
