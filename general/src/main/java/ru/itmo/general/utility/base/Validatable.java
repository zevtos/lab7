package ru.itmo.general.utility.base;

/**
 * The {@code Validatable} interface represents objects that can be valid or not.
 * Implementing classes should provide a way to validate their state.
 *
 * @author zevtos
 */
public interface Validatable {

    /**
     * Checks if the object is valid.
     *
     * @return {@code true} if the object is valid, otherwise {@code false}
     */
    boolean validate();
}

