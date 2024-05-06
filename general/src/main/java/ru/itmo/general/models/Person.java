package ru.itmo.general.models;

import ru.itmo.general.utility.base.Validatable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The {@code Person} class represents a person object.
 * It encapsulates information about the person, including their birthday, height, passport ID, and hair color.
 *
 * @param birthday   The birthday of the person.
 * @param height     The height of the person.
 * @param passportID The passport ID of the person.
 * @param hairColor  The hair color of the person.
 * @author zevtos
 */
public record Person(LocalDateTime birthday, Float height, String passportID,
                     Color hairColor) implements Validatable, Serializable {
    /**
     * Constructs a person object with the specified parameters.
     *
     * @param birthday   the birthday of the person
     * @param height     the height of the person
     * @param passportID the passport ID of the person
     * @param hairColor  the hair color of the person
     */
    public Person {
    }

    /**
     * Validates whether the person is valid.
     *
     * @return true if the person is valid, false otherwise
     */
    public boolean validate() {
        if (birthday != null && birthday.isAfter(LocalDateTime.now())) return false;
        if (height != null && height <= 0) return false;
        if (passportID == null) return false;
        return hairColor != null;
    }

    /**
     * Checks the equality of persons based on passport ID.
     *
     * @param o the object to compare
     * @return true if the objects are equal based on passport ID, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person that = (Person) o;
        return Objects.equals(passportID, that.passportID);
    }

    /**
     * Represents the person as a string.
     *
     * @return String representation of the person.
     */
    @Override
    public String toString() {
        return "Person{" +
                "\n\t\tbirthday=" + (birthday == null ? "null" : birthday) +
                "\n\t\theight=" + (height == null ? "null" : height) +
                "\n\t\tpassportID='" + passportID + '\'' +
                "\n\t\thairColor=" + hairColor +
                "\n\t}";
    }
}
