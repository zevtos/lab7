package ru.itmo.server.utility.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static ru.itmo.server.utility.crypto.SaltGenerator.generateSalt;

/**
 * A utility class for hashing passwords using SHA-256 algorithm with salt.
 *
 * @author zevtos
 */
public class PasswordHashing {

    /**
     * Hashes the given password using SHA-256 algorithm with a randomly generated salt.
     *
     * @param password The password to hash.
     * @return An array containing the hashed password and the salt used for hashing.
     */
    public static String[] hashPassword(String password) {
        String salt = generateSalt(16);
        String hashedPassword = hashPassword(password, salt);
        return new String[]{hashedPassword, salt};
    }

    /**
     * Hashes the given password using SHA-256 algorithm with the provided salt.
     *
     * @param password The password to hash.
     * @param salt     The salt used for hashing.
     * @return The hashed password.
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((salt + password).getBytes());
            byte[] hashedBytes = md.digest();
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm SHA-256 not found");
            return null;
        }
    }

    /**
     * Verifies the given input password against the hashed password using the provided salt.
     *
     * @param inputPassword  The input password to verify.
     * @param salt           The salt used for hashing.
     * @param hashedPassword The hashed password to compare against.
     * @return true if the input password matches the hashed password, false otherwise.
     */
    public static boolean verifyPassword(String inputPassword, String salt, String hashedPassword) {
        String hashedInputPassword = hashPassword(inputPassword, salt);
        return hashedInputPassword != null && hashedInputPassword.equals(hashedPassword);
    }
}
