package ru.itmo.general.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class User {
    @Getter @Setter
    private Integer id;
    @Getter
    private String username;
    @Getter
    private String passwordHash;
    @Getter
    private String salt;
    @Getter
    private LocalDateTime registrationDate;
    @Getter
    private LocalDateTime lastLoginDate;


    public User(String username, String passwordHash, String salt, LocalDateTime registrationDate) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.registrationDate = registrationDate;
    }

    public User(Integer id, String username, String passwordHash, String salt, LocalDateTime registrationDate, LocalDateTime lastLoginDate) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.registrationDate = registrationDate;
        this.lastLoginDate = lastLoginDate;
    }

    @Override
    public String toString() {
        return "User{" +
                ", username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", salt='" + salt + '\'' +
                ", registrationDate=" + registrationDate +
                ", lastLoginDate=" + lastLoginDate +
                '}';
    }

    public boolean validate() {
        if (passwordHash == null || passwordHash.length() < 8) {
            return false;
        }
        if (salt == null || salt.length() < 8) {
            return false;
        }
        if (registrationDate == null) {
            return false;
        }
        if (lastLoginDate == null) {
            return false;
        }
        return true;
    }
}
