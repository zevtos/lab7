package ru.itmo.general.models.forms;

import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidScriptInputException;

/**
 * The {@code Form} class represents an abstract form for collecting user input data.
 * It is used to build an object based on the user's input data.
 *
 * @param <T> the type of object to be created
 * @author zevtos
 */
public abstract class Form<T> {
    /**
     * Builds an object based on the user's input data.
     *
     * @return the created object
     * @throws InvalidScriptInputException if invalid data is entered in a script
     * @throws InvalidFormException        if invalid data is entered manually
     */
    public abstract T build() throws InvalidScriptInputException, InvalidFormException;
}
