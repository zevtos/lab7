package ru.itmo.general.utility.base;

public interface Accessible {
    boolean checkOwnership(int ticketId, int userId);
}
