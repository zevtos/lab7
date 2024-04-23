package ru.itmo.general.exceptions;

/**
 * Выбрасывается, если в коллекции есть объект с таким же ID.
 *
 * @author zevtos
 */
public class DuplicateException extends Exception {
    private Object duplicateObject;

    /**
     * Конструктор с объектом-дубликатом.
     *
     * @param obj Объект-дубликат.
     */
    public DuplicateException(Object obj) {
        this.duplicateObject = obj;
    }

    /**
     * Пустой конструктор.
     */
    public DuplicateException() {}

    /**
     * Получить объект-дубликат.
     *
     * @return Объект-дубликат.
     */
    public Object getDuplicateObject() {
        return duplicateObject;
    }
}
