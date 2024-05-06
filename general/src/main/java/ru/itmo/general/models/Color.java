package ru.itmo.general.models;

import java.io.Serializable;

/**
 * The {@code Color} enum represents possible colors.
 * It includes the colors GREEN, BLACK, BLUE, and YELLOW.
 *
 * @author zevtos
 */
public enum Color implements Serializable {
    GREEN,
    BLACK,
    BLUE,
    YELLOW;

    /**
     * Returns a string containing all the names of colors.
     *
     * @return A string containing all the names of colors.
     */
    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var color : values()) {
            nameList.append(color.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}
