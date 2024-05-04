package ru.itmo.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ru.itmo.server.managers.ConnectionManager.*;

import java.sql.*;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger("UserDAO");
    private static final String CREATE_USERS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY," +
            "username VARCHAR(50) NOT NULL," +
            "password_hash VARCHAR(256) NOT NULL," +
            "salt VARCHAR(16) NOT NULL," +
            "registration_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "last_login TIMESTAMP)";
    private static final String SELECT_ALL_USERS_SQL = "SELECT * FROM users";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER_BY_SQL = "INSERT INTO users (username," +
            " password_hash," +
            " salt," +
            " registration_date" +
            ", last_login" +
            ") VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_BY_ID_SQL = "UPDATE users SET username = ?," +
            " password_hash = ?," +
            " last_login_date = ?" +
            " WHERE id = ?";
    private final Connection connection;

    public UserDAO() {
        connection = getConnection();
    }

    public void getAllUsers() {
        Connection connection = getConnection();
        assert connection != null : "Connection is null";
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String passwordHash = resultSet.getString("password_hash");
                String salt = resultSet.getString("salt");
                String registrationDate = resultSet.getString("registration_date");
                String lastLogin = resultSet.getString("last_login");

                // Output user details
                System.out.println("User ID: " + id);
                System.out.println("Username: " + username);
                System.out.println("Password Hash: " + passwordHash);
                System.out.println("Salt: " + salt);
                System.out.println("Registration Date: " + registrationDate);
                System.out.println("Last Login: " + lastLogin);
                System.out.println();
            }
        } catch (SQLException e) {
            LOGGER.error("Error while fetching users: {}", e.getMessage());
        }
    }

    public boolean insertUser(String username, String passwordHash, String salt, LocalDateTime registrationDate, LocalDateTime lastLoginDate) {
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

    public void createTablesIfNotExist() {
        executeUpdate(connection, CREATE_USERS_TABLE_SQL);
    }
}
