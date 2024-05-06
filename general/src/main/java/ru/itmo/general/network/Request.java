package ru.itmo.general.network;

import java.util.Objects;

/**
 * The {@code Request} class represents a request sent over the network.
 * It encapsulates information about a command to be executed, along with optional data associated with the command.
 * Requests can be constructed with different combinations of parameters to convey different types of information.
 *
 * @author zevtos
 */
public class Request extends Sendable {

    /**
     * Constructs a request with the specified success status, command name, and data payload.
     *
     * @param success the success status of the request
     * @param name    the name of the command associated with the request
     * @param data    the data payload associated with the request
     */
    public Request(boolean success, String name, Object data) {
        super(success, name, data);
    }

    /**
     * Constructs a request with the specified command name and data payload, assuming success.
     *
     * @param name the name of the command associated with the request
     * @param data the data payload associated with the request
     */
    public Request(String name, Object data) {
        this(true, name, data);
    }

    /**
     * Returns the name of the command associated with the request.
     *
     * @return the name of the command
     */
    public String getCommand() {
        return getMessage();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param o the reference object with which to compare
     * @return true if this object is the same as the o argument; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(getCommand(), request.getCommand());
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCommand());
    }

    /**
     * Returns a string representation of the request.
     * If a data payload is present, it is appended to the string representation.
     *
     * @return a string representation of the request
     */
    @Override
    public String toString() {
        return "Request{" +
                (isSuccess() ? "" : "Ошибка при выполнении команды") +
                "command='" + getCommand() + '\'' +
                (getData() != null ? "data=" + getData() : "") +
                '}';
    }
}
