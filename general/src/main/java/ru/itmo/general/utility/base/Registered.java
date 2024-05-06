package ru.itmo.general.utility.base;

import ru.itmo.general.models.User;

/**
 * The {@code Registered} interface represents the functionality related to user registration and authentication.
 * Classes implementing this interface handle user registration, password verification, etc.
 *
 * @author zevtos
 */
public interface Registered {

    /**
     * Inserts a new user with the given username and password into the system.
     *
     * @param username the username of the user to be registered
     * @param password the password of the user to be registered
     * @return the newly registered user object, or {@code null} if registration fails
     */
    User insertUser(String username, String password);

    /**
     * Verifies the password of the user with the given username.
     *
     * @param userName the username of the user
     * @param password the password to be verified
     * @return {@code true} if the password is correct for the given username, {@code false} otherwise
     */
    boolean verifyUserPassword(String userName, String password);
}
