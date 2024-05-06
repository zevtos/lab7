package ru.itmo.server.utility.crypto;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * A utility class for generating salt strings for cryptographic purposes.
 *
 * @author zevtos
 */
public class SaltGenerator {

    /**
     * Generates a random salt string of the specified length.
     *
     * @param length The length of the salt string to generate.
     * @return A randomly generated salt string.
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
