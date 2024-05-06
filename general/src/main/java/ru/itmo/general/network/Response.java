package ru.itmo.general.network;

/**
 * The {@code Response} class represents a response sent over the network.
 * It encapsulates information about the success or failure of an operation, along with an optional message and data payload.
 * Responses can be constructed with different combinations of parameters to convey different types of information.
 *
 * @author zevtos
 */
public class Response extends Sendable {

    /**
     * Constructs a response with the specified success status, message, and data payload.
     *
     * @param success the success status of the response
     * @param message the message associated with the response
     * @param data    the data payload associated with the response
     */
    public Response(boolean success, String message, Object data) {
        super(success, message, data);
    }

    /**
     * Constructs a response with the specified success status and message.
     *
     * @param success the success status of the response
     * @param message the message associated with the response
     */
    public Response(boolean success, String message) {
        super(success, message, null);
    }

    /**
     * Constructs a response with the specified success status.
     *
     * @param success the success status of the response
     */
    public Response(boolean success) {
        super(success, null, null);
    }

    /**
     * Returns a string representation of the response.
     * If a message and data payload are present, they are appended to the string representation.
     *
     * @return a string representation of the response
     */
    @Override
    public String toString() {
        return ((message != null) ? message : "") + (data != null ? ((message != null) ? '\n' + data.toString() : data.toString()) : "");
    }
}
