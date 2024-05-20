package ru.itmo.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.models.*;
import ru.itmo.general.utility.base.Accessible;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.itmo.server.managers.ConnectionManager.*;

/**
 * The TicketDAO class provides methods for interacting with the tickets table in the database.
 * It handles ticket creation, retrieval, updating, and removal.
 *
 * @author zevtos
 */
public class TicketDAO implements Accessible {
    private static final Logger LOGGER = LoggerFactory.getLogger("TicketDAO");
    private static final String SELECT_ALL_TICKETS_SQL = "SELECT * FROM tickets";
    private static final String CREATE_TICKETS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS tickets (" +
            "id SERIAL PRIMARY KEY," +
            "name VARCHAR NOT NULL," +
            "coordinates_x DOUBLE PRECISION NOT NULL," +
            "coordinates_y FLOAT NOT NULL," +
            "creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "price DOUBLE PRECISION NOT NULL," +
            "discount BIGINT CHECK (discount IS NULL OR (discount > 0 AND discount <= 100))," +
            "comment VARCHAR," +
            "type VARCHAR(20)," +
            "person_birthday TIMESTAMP," +
            "person_height FLOAT," +
            "person_passport_id VARCHAR NOT NULL," +
            "person_hair_color VARCHAR(20) NOT NULL," +
            "user_id INT," +
            "FOREIGN KEY (user_id) REFERENCES users(id))";
    private static final String INSERT_TICKET_SQL = "INSERT INTO tickets (" +
            " name," +
            " coordinates_x," +
            " coordinates_y," +
            " creation_date," +
            " price," +
            " discount," +
            " comment," +
            " type," +
            " person_birthday," +
            " person_height," +
            " person_passport_id," +
            " person_hair_color," +
            " user_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String REMOVE_TICKET_SQL = "DELETE FROM tickets WHERE id = ?";
    private static final String CHECK_TICKET_OWNERSHIP_SQL = "SELECT user_id FROM tickets WHERE id = ?";
    private static final String UPDATE_TICKET_SQL = "UPDATE tickets SET " +
            "name = ?, " +
            "coordinates_x = ?, " +
            "coordinates_y = ?, " +
            "creation_date = ?, " +
            "price = ?, " +
            "discount = ?, " +
            "comment = ?, " +
            "type = ?, " +
            "person_birthday = ?, " +
            "person_height = ?, " +
            "person_passport_id = ?, " +
            "person_hair_color = ? " +
            "WHERE id = ?";

    /**
     * Adds a new ticket to the database.
     *
     * @param ticket The ticket to be added.
     * @param userId The ID of the user adding the ticket.
     * @return The ID of the newly added ticket if successful, otherwise -1.
     */
    public int addTicket(Ticket ticket, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(INSERT_TICKET_SQL, Statement.RETURN_GENERATED_KEYS)) {
            set(statement, ticket);

            int rowsAffected = executePrepareUpdate(statement);
            if (rowsAffected > 0) {
                // Get the generated keys (which include the ID of the newly added ticket)
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    // Return the ID of the newly added ticket
                    return generatedKeys.getInt(1);
                } else {
                    // No generated keys found
                    LOGGER.error("Failed to retrieve generated keys after adding ticket");
                    return -1;
                }
            } else {
                LOGGER.error("No rows were affected while adding ticket");
                return -1;
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while adding ticket, continuing without adding ticket");
            return -1;
        } catch (SQLException e) {
            LOGGER.error("Error while adding ticket {}", e.getMessage());
            return -1;
        }
    }

    /**
     * Adds a collection of tickets to the database.
     *
     * @param tickets The collection of tickets to be added.
     * @param userId  The ID of the user adding the tickets.
     */
    public void addTickets(Collection<Ticket> tickets, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_TICKET_SQL)) {
            for (Ticket ticket : tickets) {
                set(statement, ticket);
                statement.addBatch();
            }
            int[] results = statement.executeBatch();
            // Check the results array to determine the success of each insertion
            for (int result : results) {
                if (result <= 0) {
                    return; // At least one insertion failed
                }
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while adding tickets, continuing without adding tickets");
        } catch (SQLException e) {
            LOGGER.error("Error while adding tickets {}", e.getMessage());
        }
    }

    private void set(PreparedStatement statement, Ticket ticket) throws SQLException {
        statement.setString(1, ticket.getName());
        statement.setDouble(2, ticket.getCoordinates().x());
        statement.setFloat(3, ticket.getCoordinates().y());
        statement.setTimestamp(4, Timestamp.from(ticket.getCreationDate().toInstant()));
        statement.setDouble(5, ticket.getPrice());
        if (ticket.getDiscount() != null) {
            statement.setLong(6, ticket.getDiscount());
        } else {
            statement.setNull(6, Types.BIGINT);
        }
        statement.setString(7, ticket.getComment());
        statement.setString(8, ticket.getType().toString());
        statement.setTimestamp(9, Timestamp.from(ticket.getPerson().birthday().toInstant(ZoneOffset.UTC)));
        statement.setFloat(10, ticket.getPerson().height());
        statement.setString(11, ticket.getPerson().passportID());
        statement.setString(12, ticket.getPerson().hairColor().toString());
        statement.setInt(13, ticket.getUserId());
    }

    /**
     * Retrieves all tickets from the database.
     *
     * @return A list of all tickets retrieved from the database.
     */
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_TICKETS_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Ticket ticket = extractTicketFromResultSet(resultSet);
                tickets.add(ticket);
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while getting all tickets, continuing without getting all tickets");
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving tickets from the database: {}", e.getMessage());
        }
        return tickets;
    }

    /**
     * Removes a ticket from the database by its ID.
     *
     * @param ticketId The ID of the ticket to be removed.
     * @return true if the ticket was successfully removed, false otherwise.
     */
    public boolean removeTicketById(int ticketId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(REMOVE_TICKET_SQL)) {
            statement.setInt(1, ticketId);
            return executePrepareUpdate(statement) > 0;
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while removing ticket, continuing without removing ticket");
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while deleting ticket with ID {}: {}", ticketId, e.getMessage());
            return false;
        }
    }

    /**
     * Updates a ticket in the database.
     *
     * @param ticket The ticket with updated information.
     * @return true if the ticket was successfully updated, false otherwise.
     */
    public boolean updateTicket(Ticket ticket) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_TICKET_SQL)) {

            set(statement, ticket);
            statement.setInt(13, ticket.getId());
            return executePrepareUpdate(statement) > 0;
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while updating ticket, continuing without updating ticket");
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while updating ticket {}: {}", ticket.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Creates the tickets table in the database if it does not already exist.
     */
    public void createTablesIfNotExist() {
        Connection connection = getConnection();
        executeUpdate(connection, CREATE_TICKETS_TABLE_SQL);
    }

    private Ticket extractTicketFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        double coordinatesX = resultSet.getDouble("coordinates_x");
        float coordinatesY = resultSet.getFloat("coordinates_y");
        Timestamp creationDateTimestamp = resultSet.getTimestamp("creation_date");
        ZonedDateTime creationDate = creationDateTimestamp.toInstant().atZone(ZoneOffset.UTC);
        double price = resultSet.getDouble("price");
        Long discount = resultSet.getLong("discount");
        if (resultSet.wasNull()) {
            discount = null;
        }
        String comment = resultSet.getString("comment");
        String typeStr = resultSet.getString("type");
        TicketType type = typeStr != null ? TicketType.valueOf(typeStr) : null;

        Timestamp personBirthdayTimestamp = resultSet.getTimestamp("person_birthday");
        LocalDateTime personBirthday = personBirthdayTimestamp != null ?
                personBirthdayTimestamp.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime() : null;

        float personHeight = resultSet.getFloat("person_height");
        String personPassportID = resultSet.getString("person_passport_id");
        String personHairColorStr = resultSet.getString("person_hair_color");
        Color personHairColor = Color.valueOf(personHairColorStr);

        // Assuming Ticket constructor accepts all these parameters
        return new Ticket(id, name, new Coordinates(coordinatesX, coordinatesY), creationDate, price,
                discount, comment, type, new Person(personBirthday, personHeight, personPassportID, personHairColor));
    }

    /**
     * Checks if a ticket belongs to a specific user.
     *
     * @param ticketId The ID of the ticket.
     * @param userId   The ID of the user.
     * @return true if the ticket belongs to the user, false otherwise.
     */
    @Override
    public boolean checkOwnership(int ticketId, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_TICKET_OWNERSHIP_SQL)) {
            statement.setInt(1, ticketId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int ownerId = resultSet.getInt("user_id");
                return ownerId == userId;
            } else {
                return false;
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while checking ownership of ticket with ID {}: {}",
                    ticketId, exception.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while checking ownership of ticket with ID {}: {}", ticketId, e.getMessage());
            return false;
        }
    }


}
