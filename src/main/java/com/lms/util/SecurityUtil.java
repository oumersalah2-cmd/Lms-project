package main.java.com.lms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SOLID:
 * - SRP: Handles cryptographic hashing only.
 * - DIP: Used by higher-level factories, not models.
 */
public final class SecurityUtil {

    private SecurityUtil() {
        // Prevent instantiation
    }
    public static String hash(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input to hash cannot be null");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(
                    input.trim().getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder hex = new StringBuilder();
            for (byte b : hashedBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean verifyPassword(String rawPassword, String storedHash) {
        // If either is null, it's an immediate fail
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        
       
        String newHash = hash(rawPassword); 
        boolean match = newHash.equalsIgnoreCase(storedHash.trim());
        
        if (!match) {
            System.out.println("DEBUG: Password mismatch!");
            System.out.println("New Hash generated: [" + newHash + "]");
            System.out.println("Stored Hash found:  [" + storedHash.trim() + "]");
        }
        
        return match;
    }
}
