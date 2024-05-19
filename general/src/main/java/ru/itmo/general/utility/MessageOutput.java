package ru.itmo.general.utility;

/**
 * The {@code MessageOutput} interface provides methods for outputting messages and errors.
 * Implementing classes should provide specific implementations for different output targets,
 * such as console or GUI.
 */
public interface MessageOutput {

    /**
     * Prints a message.
     *
     * @param message the message to be printed
     */
    void print(String message);

    /**
     * Prints a message followed by a new line.
     *
     * @param message the message to be printed
     */
    void println(String message);

    /**
     * Prints an error message.
     *
     * @param errorMessage the error message to be printed
     */
    void printError(String errorMessage);
}
