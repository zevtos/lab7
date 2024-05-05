package ru.itmo.server.utility.base;

/**
 * Интерфейс для объектов, которые могут быть валидными или нет.
 *
 * @author zevtos
 */
public interface Validatable {
    /**
     * Проверяет, является ли объект валидным.
     *
     * @return true, если объект валиден, иначе false.
     */
    boolean validate();
}
