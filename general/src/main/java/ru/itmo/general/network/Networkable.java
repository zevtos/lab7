package ru.itmo.general.network;

import java.io.Serializable;

public abstract class Networkable implements Serializable {
    final boolean success;
    final Object data;
    final String message;
    public Networkable(final boolean success, String message, final Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    public Object getData() {
        return data;
    }
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}
