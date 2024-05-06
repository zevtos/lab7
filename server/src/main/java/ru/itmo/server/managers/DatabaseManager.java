package ru.itmo.server.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.server.dao.TicketDAO;
import ru.itmo.server.dao.UserDAO;

import java.sql.Connection;
import java.sql.SQLException;

import static ru.itmo.server.managers.ConnectionManager.*;

/**
 * Manages the database operations, including database creation, table creation, and user management.
 *
 * @author zevtos
 */
public class DatabaseManager {
    private static final UserDAO userDAO = new UserDAO();
    private static final TicketDAO ticketDAO = new TicketDAO();
    private static final Logger logger = LoggerFactory.getLogger("DatabaseManager");

    /**
     * Creates the database if it does not already exist and initializes tables.
     */
    public static void createDatabaseIfNotExists() {
        try (Connection connection = getConnection()) {
            if (connection != null) {
                boolean databaseExists = checkDatabaseExists(connection);
                if (!databaseExists) {
                    executeUpdate(connection, "CREATE DATABASE " + DB_NAME);
                    logger.info("Database and tables created successfully.");
                } else {
                    logger.info("Database already exists.");
                }
                createTablesIfNotExist(connection);
            } else {
                logger.error("Failed to establish connection to the database.");
            }
        } catch (SQLException e) {
            logger.error("Error while creating database: {}", e.getMessage());
        }
    }

    /**
     * Checks if the database already exists.
     *
     * @param connection The database connection.
     * @return True if the database exists, false otherwise.
     * @throws SQLException If an SQL error occurs.
     */
    private static boolean checkDatabaseExists(Connection connection) throws SQLException {
        return connection.getMetaData().getCatalogs()
                .next(); // Check if the database exists by attempting to move to the first entry
    }

    /**
     * Creates necessary tables if they do not already exist.
     *
     * @param connection The database connection.
     */
    public static void createTablesIfNotExist(Connection connection) {
        if (connection != null) {
            userDAO.createTablesIfNotExist();
            ticketDAO.createTablesIfNotExist();
            logger.info("Tables created successfully (if not existed).");
        } else {
            logger.error("Connection is null.");
        }
    }

}
