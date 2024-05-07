package ru.itmo.general.exceptions;

/**
 * Выбрасывается, если в форме создан невалидный объект.
 *
 * @author zevtos
 */
public class InvalidFormException extends Exception {
    public InvalidFormException() {
        super();
    }

    public InvalidFormException(String message) {
        super(message);
    }
}
