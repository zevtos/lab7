package ru.itmo.general.models;

import lombok.Getter;
import ru.itmo.general.utility.base.Element;
import ru.itmo.general.utility.base.Validatable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс, представляющий объект персоны.
 * @author zevtos
 */
public class Person implements Validatable, Serializable {
    @Getter
    private LocalDateTime birthday; //Поле может быть null
    @Getter
    private Float height; //Поле может быть null, Значение поля должно быть больше 0
    @Getter
    private String passportID; //Поле не может быть null
    @Getter
    private Color hairColor; //Поле не может быть null

    public Person(LocalDateTime birthday, Float height, String passportID, Color hairColor) {
        this.birthday = birthday;
        this.height = height;
        this.passportID = passportID;
        this.hairColor = hairColor;
    }

    /**
     * Представляет персону в виде строки.
     * @return Строковое представление персоны.
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

    /**
     * Проверяет, является ли персона валидной.
     * @return true, если персона валидна, иначе false.
     */
    public boolean validate() {
        if(birthday != null && birthday.isAfter(LocalDateTime.now())) return false;
        if (height != null && height <= 0) return false;
        if (passportID == null) return false;
        return hairColor != null;
    }

    /**
     * Проверяет равенство персон по паспортному ID.
     * @param o Объект для сравнения.
     * @return true, если объекты равны по паспортному ID, иначе false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person that = (Person) o;
        return Objects.equals(passportID, that.passportID);
    }
}
