package ru.itmo.general.network;


public class Response extends Networkable {

    public Response(boolean success, String message, Object data) {
        super(success, message, data);
    }

    public Response(boolean success, String message) {
        super(success, message, null);
    }

    public Response(boolean success) {
        super(success, null, null);
    }

    @Override
    public String toString() {
        return ((message != null) ? message : "") + (data != null ? ((message != null) ? '\n' + data.toString() : data.toString()) : "");
    }
}
