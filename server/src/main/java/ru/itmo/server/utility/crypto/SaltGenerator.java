package ru.itmo.server.utility.crypto;

import java.security.SecureRandom;
import java.util.Base64;

public class SaltGenerator {

    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
