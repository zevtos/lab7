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
}
