package ru.itmo.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.models.User;
import ru.itmo.general.utility.base.Registered;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import static ru.itmo.server.managers.ConnectionManager.*;
import static ru.itmo.server.utility.crypto.PasswordHashing.hashPassword;
import static ru.itmo.server.utility.crypto.SaltGenerator.generateSalt;

/**
 * The UserDAO class provides methods for interacting with the users table in the database.
 * It handles user creation, retrieval, updating, and password verification.
 *
 * @author zevtos
 */
public class UserDAO implements Registered {
    private static final Logger LOGGER = LoggerFactory.getLogger("UserDAO");
    private static final String CREATE_USERS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY," +
            "username VARCHAR(50) UNIQUE NOT NULL," +
            "password_hash VARCHAR(256) NOT NULL," +
            "salt VARCHAR(32) NOT NULL," +
            "registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "last_login TIMESTAMP)";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT * FROM users " +
            "WHERE username = ?";
    private static final String INSERT_USER_BY_SQL = "INSERT INTO users (username," +
            " password_hash," +
            " salt," +
            " registration_date" +
            ", last_login" +
            ") VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_BY_ID_SQL = "UPDATE users SET username = ?," +
            " password_hash = ?," +
            " last_login = ?" +
            " WHERE id = ?";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE username = ?";
    private static final String UPDATE_USER_BY_USERNAME_AND_PASSWORD_SQL = "UPDATE users SET " +
            "password_hash = ?," +
            " last_login = ?" +
            " WHERE username = ?";
    private static final String SELECT_SALT_BY_USERNAME_SQL = "SELECT salt FROM users" +
            " WHERE username = ?";
    private final Connection connection;

    /**
     * Constructs a new UserDAO object.
     * Initializes the database connection.
     */
    public UserDAO() {
        connection = getConnection();
    }

    /**
     * Inserts a new user into the database.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @return The created User object if successful, otherwise null.
     */
    public User insertUser(String username, String password) {
        // Generate a random salt
        String salt = generateSalt(16);

        // Hash the password with the salt
        String hashedPassword = hashPassword(password, salt);

        // Create a new user with the provided details
        LocalDateTime registrationDate = LocalDateTime.now();
        User user = new User(username, hashedPassword, salt, registrationDate);

        // Insert the user into the database
        if (insertUser(user.getUsername(), user.getPasswordHash(),
                user.getSalt(), registrationDate, registrationDate)) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Inserts a new user into the database with specified details.
     *
     * @param username         The username of the new user.
     * @param passwordHash     The hashed password of the new user.
     * @param salt             The salt used for hashing the password.
     * @param registrationDate The registration date of the new user.
     * @param lastLoginDate    The last login date of the new user.
     * @return true if the user was successfully inserted, otherwise false.
     */
    public boolean insertUser(String username, String passwordHash,
                              String salt, LocalDateTime registrationDate,
                              LocalDateTime lastLoginDate) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER_BY_SQL)) {
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, salt);
            statement.setObject(4, registrationDate);
            statement.setObject(5, lastLoginDate);
            return executePrepareUpdate(statement) > 0;
        } catch (SQLException e) {
            LOGGER.error("Error while inserting user: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a user from the database by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, otherwise null.
     */
    public User getUserByUsername(String username) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_USERNAME_SQL)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String storedUsername = resultSet.getString("username");
                    String passwordHash = resultSet.getString("password_hash");
                    String salt = resultSet.getString("salt");
                    LocalDateTime registrationDate = resultSet.getTimestamp("registration_date").toLocalDateTime();

                    return new User(id, storedUsername, passwordHash, salt, registrationDate);
                } else {
                    return null; // User not found
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving user by username: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Updates a user in the database with new details.
     *
     * @param userId           The ID of the user to update.
     * @param newUsername      The new username of the user.
     * @param newPasswordHash  The new hashed password of the user.
     * @param newLastLoginDate The new last login date of the user.
     * @return true if the user was successfully updated, otherwise false.
     */
    public boolean updateUser(int userId, String newUsername, String newPasswordHash, LocalDateTime newLastLoginDate) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER_BY_ID_SQL)) {
            statement.setString(1, newUsername);
            statement.setString(2, newPasswordHash);
            statement.setObject(3, newLastLoginDate);
            statement.setInt(4, userId);
            return executePrepareUpdate(statement) > 0;
        } catch (SQLException e) {
            LOGGER.error("Error while updating user: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Updates the password of a user in the database.
     *
     * @param username    The username of the user.
     * @param newPassword The new password to set for the user.
     * @return true if the password was successfully updated, otherwise false.
     */
    public boolean updateUser(String username, String newPassword) {

        try {
            // Retrieve salt from the database
            String salt = null;
            try (PreparedStatement selectStatement = connection.prepareStatement(SELECT_SALT_BY_USERNAME_SQL)) {
                selectStatement.setString(1, username);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        salt = resultSet.getString("salt");
                    } else {
                        LOGGER.error("User with username {} not found.", username);
                        return false; // User not found
                    }
                }
            } catch (NullPointerException exception) {
                LOGGER.error("Null pointer exception while fetching user by username: {}", exception.getMessage());
            }

            // Hash the new password using the retrieved salt
            String newPasswordHash = hashPassword(newPassword, salt);

            // Update the user's password in the database
            try (PreparedStatement updateStatement =
                         connection.prepareStatement(UPDATE_USER_BY_USERNAME_AND_PASSWORD_SQL)) {
                updateStatement.setString(1, newPasswordHash);
                updateStatement.setObject(2, LocalDateTime.now());
                updateStatement.setString(3, username);

                return executePrepareUpdate(updateStatement) > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Error while updating user: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates the users table in the database if it doesn't exist.
     */
    public void createTablesIfNotExist() {
        executeUpdate(connection, CREATE_USERS_TABLE_SQL);
    }

    /**
     * Verifies the password of a user by their username.
     *
     * @param username The username of the user.
     * @param password The password to verify.
     * @return true if the password is correct, otherwise false.
     */
    public boolean verifyUserPassword(String username, String password) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID_SQL)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPasswordHash = resultSet.getString("password_hash");
                    String storedSalt = resultSet.getString("salt");
                    String enteredPasswordHash = hashPassword(password, storedSalt);
                    return storedPasswordHash.equals(enteredPasswordHash);
                } else {
                    return false; // User not found
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error while verifying user password: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Verifies the password of a user.
     *
     * @param user     The User object representing the user.
     * @param password The password to verify.
     * @return true if the password is correct, otherwise false.
     */
    public boolean verifyUserPassword(User user, String password) {
        String storedPasswordHash = user.getPasswordHash();
        String storedSalt = user.getSalt();
        String enteredPasswordHash = hashPassword(password, storedSalt);
        return storedPasswordHash.equals(enteredPasswordHash);
    }
}
