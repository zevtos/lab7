package ru.itmo.general.exceptions;

/**
 * Выбрасывается, если что-то не найдено.
 *
 * @author zevtos
 */
public class NotFoundException extends Exception {
    private String message;

    public NotFoundException() {
        this.message = "";
    }

    public NotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
