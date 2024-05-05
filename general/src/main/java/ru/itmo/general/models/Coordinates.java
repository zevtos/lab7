package ru.itmo.general.models;


import lombok.Getter;
import ru.itmo.general.utility.base.Validatable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс, представляющий координаты.
 *
 * @author zevtos
 */
public class Coordinates implements Validatable, Serializable {
    @Getter
    private double x;
    @Getter
    private Float y; //Значение поля должно быть больше -420, Поле не может быть null

    /**
     * Создает объект координат с заданными значениями x и y.
     *
     * @param x1 Значение координаты x.
     * @param y1 Значение координаты y.
     */
    public Coordinates(double x1, Float y1) {
        x = x1;
        y = y1;
    }

    /**
     * Представляет координаты в виде строки.
     *
     * @return Строковое представление координат.
     */
    @Override
    public String toString() {
        return x + ";" + y;
    }

    /**
     * Проверяет валидность координат.
     *
     * @return true, если координаты валидны, иначе false.
     */
    public boolean validate() {
        return y != null && y > -420;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Double.compare(that.x, x) == 0 &&
                Objects.equals(y, that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
