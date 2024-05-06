package ru.itmo.general.utility.base;

/**
 * The {@code Accessible} interface represents objects that can be accessed by users with certain ownership rights.
 * Implementing classes should provide a way to check ownership of an object.
 *
 * @author zevtos
 */
public interface Accessible {

    /**
     * Checks if a user has ownership rights over a specific object.
     *
     * @param id     the ID of the object
     * @param userId the ID of the user
     * @return {@code true} if the user has ownership rights over the object, otherwise {@code false}
     */
    boolean checkOwnership(int id, int userId);
}
