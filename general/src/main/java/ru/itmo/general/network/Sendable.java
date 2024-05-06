package ru.itmo.general.network;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The {@code Sendable} class represents an object that can be sent over the network.
 * It encapsulates information about the success status, message, data payload, and optionally user credentials.
 * This class serves as an abstract base class for specific types of sendable objects.
 *
 * @author zevtos
 */
@Getter
@Setter
public abstract class Sendable implements Serializable {
    /**
     * Indicates whether the operation associated with this object was successful.
     */
    protected final boolean success;

    /**
     * Additional message associated with the operation, typically used for error messages.
     */
    protected final String message;

    /**
     * Data payload associated with the object, which can vary depending on the specific subclass.
     */
    protected final Object data;

    /**
     * Login credentials associated with the request, if applicable.
     */
    protected String login;

    /**
     * Password credentials associated with the request, if applicable.
     */
    protected String password;

    /**
     * User ID associated with the request, if applicable.
     */
    protected Integer userId;

    /**
     * Constructs a sendable object with the specified success status, message, and data payload.
     *
     * @param success indicates whether the operation associated with this object was successful
     * @param message additional message associated with the operation
     * @param data    data payload associated with the object
     */
    public Sendable(final boolean success, String message, final Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
