package ru.itmo.general.utility.base;

import java.io.Serializable;

/**
 * Абстрактный класс для элементов, реализующих интерфейс Comparable и Validatable.\
 * @author zevtos
 */
public abstract class Element implements Comparable<Element>, Validatable, Serializable {
    /**
     * Возвращает идентификатор элемента.
     * @return Идентификатор элемента.
     */
    abstract public int getId();
    abstract public String getName();
}
