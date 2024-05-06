package ru.itmo.general.commands;

import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Interface for all executable commands.
 *
 * @author zevtos
 */
public interface Executable {
    /**
     * Executes the command with the specified arguments.
     *
     * @param arguments the command arguments
     * @return the response indicating the result of the command execution
     */
    default Response execute(Request arguments) {
        return null;
    }

    /**
     * Executes the command with the specified arguments.
     *
     * @param arguments the command arguments as an array of strings
     * @return the request indicating the result of the command execution
     * and contains necessary data for server
     */
    default Request execute(String[] arguments) {
        return null;
    }
}

