package ru.itmo.general.exceptions;

/**
 * Выбрасывается, если что-то введено вне диапазона допустимых значений.
 *
 * @author zevtos
 */
public class InvalidRangeException extends Exception {
    private String message;

    /**
     * Конструктор по умолчанию.
     */
    public InvalidRangeException() {
    }

    /**
     * Конструктор с сообщением об ошибке.
     *
     * @param s сообщение об ошибке
     */
    public InvalidRangeException(String s) {
        this.message = s;
    }

    /**
     * Возвращает сообщение об ошибке.
     *
     * @return сообщение об ошибке
     */
    public String getMessage() {
        return message;
    }
}
