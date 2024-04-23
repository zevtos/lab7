package ru.itmo.server.utility.base;

/**
 * Абстрактный класс для элементов, реализующих интерфейс Comparable и Validatable.\
 * @author zevtos
 */
public abstract class Element implements Comparable<Element>, Validatable {
    /**
     * Возвращает идентификатор элемента.
     * @return Идентификатор элемента.
     */
    abstract public int getId();
}
