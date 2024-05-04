package ru.itmo.server.utility.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static ru.itmo.server.utility.crypto.SaltGenerator.generateSalt;

public class PasswordHashing {
    public static String[] hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");


            String salt = generateSalt(16);

            String hashedPassword = hashPassword(password, salt);

            return new String[]{hashedPassword, salt};
        } catch (NoSuchAlgorithmException ignored) {
            System.err.println("Algorithm SHA-256 not found");
            return null;
        }
    }

    public static String hashPassword(String password, String salt) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add salt and password bytes to digest
            md.update((salt + password).getBytes());

            // Get the hashed bytes
            byte[] hashedBytes = md.digest();

            // Convert byte array to base64 representation
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm SHA-256 not found");
            return null;
        }
    }
}
