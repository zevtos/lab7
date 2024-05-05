package ru.itmo.general.exceptions;

/**
 * Выбрасывается, если что-то не найдено.
 *
 * @author zevtos
 */
public class ResponseException extends Exception {
    private String message;

    public ResponseException() {
        this.message = "";
    }

    public ResponseException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
