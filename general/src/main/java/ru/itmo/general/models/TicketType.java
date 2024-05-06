package ru.itmo.general.models;

import java.io.Serializable;

/**
 * The {@code TicketType} enum represents the types of tickets available.
 * It includes the types VIP, USUAL, and CHEAP.
 *
 * @author zevtos
 */
public enum TicketType implements Serializable {
    VIP,
    USUAL,
    CHEAP;

    /**
     * Returns a string containing all the names of ticket types.
     *
     * @return A string containing all the names of ticket types, separated by commas.
     */
    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var ticketType : values()) {
            nameList.append(ticketType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}
