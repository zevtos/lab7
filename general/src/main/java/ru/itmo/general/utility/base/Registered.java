package ru.itmo.general.utility.base;

import ru.itmo.general.models.User;

public interface Registered {
    User insertUser(String username, String password);

    public boolean verifyUserPassword(String userName, String password);
}
