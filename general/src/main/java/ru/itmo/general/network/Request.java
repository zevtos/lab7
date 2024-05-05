package ru.itmo.general.network;

import lombok.Getter;
import lombok.Setter;
import ru.itmo.general.commands.CommandName;

import java.util.Objects;

public class Request extends Networkable {

    public Request(boolean succsess, String name, Object data) {
        super(succsess, name, data);
    }

    public Request(boolean succsess, CommandName name, Object data) {
        this(succsess, name.toString().toLowerCase(), data);
    }

    public Request(CommandName name, Object data) {
        this(true, name, data);
    }

    public Request(String name, Object data) {
        this(true, name, data);
    }

    public Request(String name) {
        this(false, name, null);
    }

    public Request() {
        this(false, "", null);
    }

    public String getCommand() {
        return getMessage();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(getCommand(), request.getCommand());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommand());
    }

    @Override
    public String toString() {
        return "Request{" +
                (isSuccess() ? "" : "Ошибка при выполнении команды") +
                "command='" + getCommand() + '\'' +
                (getData() != null ? "data=" + getData() : "") +
                '}';
    }
}
