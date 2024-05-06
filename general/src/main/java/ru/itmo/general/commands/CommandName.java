package ru.itmo.general.commands;

/**
 * Enum representing different command names.
 */
public enum CommandName {
    HELP,                // Command to display help information
    INFO,                // Command to display information about the collection
    SHOW,                // Command to display all elements of the collection
    ADD,                 // Command to add an element to the collection
    UPDATE,              // Command to update an element in the collection
    REMOVE_BY_ID,        // Command to remove an element from the collection by its ID
    CLEAR,               // Command to clear the collection
    EXIT,                // Command to exit the program
    REMOVE_FIRST,        // Command to remove the first element from the collection
    REMOVE_HEAD,         // Command to remove the head element from the collection
    ADD_IF_MIN,          // Command to add an element to the collection if it is the minimum element
    SUM_OF_PRICE,        // Command to calculate the sum of prices of all elements in the collection
    MIN_BY_DISCOUNT,     // Command to find the element with the minimum discount in the collection
    MAX_BY_NAME,         // Command to find the element with the maximum name in the collection
    HISTORY,             // Command to display command history
    EXECUTE_SCRIPT,      // Command to execute commands from a script file
    EXIT_SERVER,         // Command to exit the server
    SAVE,                // Command to save the collection to a file
    PING,                // Command to ping the server
    LOGIN,               // Command to log in
    REGISTER             // Command to register a new user
}

