package ru.itmo.general.utility.console;

/**
 * The {@code Console} interface provides methods for inputting commands and outputting results to the console.
 * It includes methods for printing objects, errors, and tables, as well as prompting for user input.
 * Implementing classes should provide implementations for these methods.
 *
 * @author zevtos
 */
public interface Console {

    /**
     * Prints an object to the console.
     *
     * @param obj the object to be printed
     */
    void print(Object obj);

    /**
     * Prints an object to the console followed by a new line.
     *
     * @param obj the object to be printed
     */
    void println(Object obj);

    /**
     * Prints an error message to the console.
     *
     * @param callingClass the class where the error occurred
     * @param obj          the error object to be printed
     */
    void printError(Class<?> callingClass, Object obj);

    /**
     * Prints two objects in a tabular format.
     *
     * @param obj1 the first object
     * @param obj2 the second object
     */
    void printTable(Object obj1, Object obj2);

    /**
     * Prints a prompt for user input.
     */
    void prompt();

    /**
     * Retrieves the prompt for user input.
     *
     * @return the prompt for user input
     */
    String getPrompt();

    /**
     * Prints an empty line to the console.
     */
    void println();

    /**
     * Prompts the user to input a password from the console and returns it.
     *
     * @param prompt the prompt for the user to input the password
     * @return a char array containing the characters of the password
     */
    char[] readPassword(String prompt);
}
