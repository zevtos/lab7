package ru.itmo.server.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class ConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ConnectionManager");
    public static final String DB_URL = "jdbc:postgresql://localhost:5432/";
    public static final String DB_NAME = "studs";
    private static final String USER = "postgres";
    private static final String PASSWORD = "";

    // Method to get a database connection
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL + DB_NAME, USER, PASSWORD);
        } catch (SQLException e) {
            logError("Connection failed", e);
            return null;
        }
    }

    // Method to close a database connection
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logError("Error closing connection", e);
            }
        }
    }

    private static void logError(String message, SQLException e) {
        if ((e == null)) {
            LOGGER.error(message);
        } else {
            LOGGER.error("{}: {}", message, e.getMessage());
        }
    }

    private static Statement createStatement(Connection connection) {
        if (connection == null) {
            logError("Connection is null", null);
            return null;
        }
        try {
            return connection.createStatement();
        } catch (SQLException e) {
            logError("Error creating statement", e);
            return null;
        }
    }

    public static void executeUpdate(Statement statement, String sql) {
        if (statement == null) {
            logError("Statement is null", null);
            return;
        }
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            logError("Error executing update", e);
        }
    }

    public static void executeUpdate(Connection connection, String sql) {
        Statement statement = createStatement(connection);
        executeUpdate(statement, sql);
    }

    public static int executePrepareUpdate(PreparedStatement statement) {
        if (statement == null) {
            logError("Statement is null", null);
            return -1;
        } else {
            try {
                return statement.executeUpdate();
            } catch (SQLException e) {
                logError("Error executing update", e);
                return -1;
            }
        }
    }
}
