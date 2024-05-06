package ru.itmo.general.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * The {@code User} class represents a user in the system.
 * It encapsulates information about the user, including their ID, username, password hash, salt, and registration date.
 * This class is used for managing user authentication and registration.
 *
 * @author zevtos
 */
@Getter
public class User {
    private final String username;
    private final String passwordHash;
    private final String salt;
    private final LocalDateTime registrationDate;
    @Setter
    private Integer id;

    /**
     * Constructs a new user object with the specified parameters.
     *
     * @param username         the username of the user
     * @param passwordHash     the hashed password of the user
     * @param salt             the salt used for password hashing
     * @param registrationDate the registration date of the user
     */
    public User(String username, String passwordHash, String salt, LocalDateTime registrationDate) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.registrationDate = registrationDate;
    }

    /**
     * Constructs a new user object with the specified parameters.
     *
     * @param id               the ID of the user
     * @param username         the username of the user
     * @param passwordHash     the hashed password of the user
     * @param salt             the salt used for password hashing
     * @param registrationDate the registration date of the user
     */
    public User(Integer id, String username, String passwordHash, String salt, LocalDateTime registrationDate) {
        this(username, passwordHash, salt, registrationDate);
        this.id = id;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return a string representation of the user
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", salt='" + salt + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }

    /**
     * Validates whether the user object is valid.
     * It checks if the password hash and salt are not null and have a minimum length of 8 characters,
     * and if the registration date is not null.
     *
     * @return true if the user object is valid, false otherwise
     */
    public boolean validate() {
        if (passwordHash == null || passwordHash.length() < 8) {
            return false;
        }
        if (salt == null || salt.length() < 8) {
            return false;
        }
        return registrationDate != null;
    }
}
