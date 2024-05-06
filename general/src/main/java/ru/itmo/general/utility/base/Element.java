package ru.itmo.general.utility.base;

import java.io.Serializable;

/**
 * The {@code Element} abstract class serves as a base for elements that implement the {@code Comparable} and {@code Validatable} interfaces.
 * It provides common functionality for such elements.
 *
 * @author zevtos
 */
public abstract class Element implements Comparable<Element>, Validatable, Serializable {

    /**
     * Returns the identifier of the element.
     *
     * @return the identifier of the element
     */
    abstract public int getId();

    /**
     * Returns the name of the element.
     *
     * @return the name of the element
     */
    abstract public String getName();
}

