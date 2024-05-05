package ru.itmo.general.network;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public abstract class Networkable implements Serializable {
    final boolean success;
    final Object data;
    final String message;
    @Getter
    @Setter
    private String login;
    @Getter
    @Setter
    private String password;
    @Getter
    @Setter
    private Integer userId = null;

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
