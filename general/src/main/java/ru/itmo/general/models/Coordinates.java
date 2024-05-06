package ru.itmo.general.models;

import ru.itmo.general.utility.base.Validatable;

import java.io.Serializable;
import java.util.Objects;

/**
 * The {@code Coordinates} class represents a set of coordinates.
 * It encapsulates the x and y coordinates of a point.
 *
 * @param x The x-coordinate of the point.
 * @param y The y-coordinate of the point.
 * @author zevtos
 */
public record Coordinates(double x, Float y) implements Validatable, Serializable {

    /**
     * Validates the coordinates.
     *
     * @return true if the coordinates are valid, false otherwise.
     */
    public boolean validate() {
        return y != null && y > -420;
    }

    /**
     * Represents the coordinates as a string.
     *
     * @return String representation of the coordinates.
     */
    @Override
    public String toString() {
        return x + ";" + y;
    }

    /**
     * Checks the equality of coordinates.
     *
     * @param o The object to compare.
     * @return true if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(that.x, x) == 0 && Objects.equals(y, that.y);
    }

    /**
     * Computes the hash code of the coordinates.
     *
     * @return The hash code value of the coordinates.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
