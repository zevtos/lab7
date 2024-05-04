package ru.itmo.server.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.dao.TicketDAO;
import ru.itmo.server.dao.UserDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

import static ru.itmo.server.managers.ConnectionManager.*;

public class DatabaseManager {
    private static final UserDAO userDAO = new UserDAO();
    private static final TicketDAO ticketDAO = new TicketDAO();
    private static final Logger logger = LoggerFactory.getLogger("DatabaseManager");

    public static void createDatabaseIfNotExists() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                boolean databaseExists = checkDatabaseExists(connection);
                if (!databaseExists) {
                    executeUpdate(connection, "CREATE DATABASE " + DB_NAME);
                    createTablesIfNotExist(connection);
                    logger.info("Database and tables created successfully.");
                } else {
                    logger.info("Database already exists.");
                }
            } else {
                logger.error("Failed to establish connection to the database.");
            }
        } catch (SQLException e) {
            logger.error("Error while creating database: {}", e.getMessage());
        }
    }

    private static boolean checkDatabaseExists(Connection connection) throws SQLException {
        return connection.getMetaData().getCatalogs()
                .next(); // Check if the database exists by attempting to move to the first entry
    }

    private static void createTablesIfNotExist(Connection connection) {
        if (connection != null) {
            userDAO.createTablesIfNotExist();
            ticketDAO.createTablesIfNotExist();
            logger.info("Tables created successfully (if not existed).");
        } else {
            logger.error("Connection is null.");
        }
    }

    public void insertUser(String username, String passwordHash, String salt,
                           LocalDateTime registrationDate, LocalDateTime lastLoginDate) {
        userDAO.insertUser(username, passwordHash, salt, registrationDate, lastLoginDate);
    }

    public void updateUser(int userId, String newUsername,
                           String newPasswordHash, LocalDateTime newLastLoginDate) {
        userDAO.updateUser(userId, newUsername, newPasswordHash, newLastLoginDate);
    }

}
