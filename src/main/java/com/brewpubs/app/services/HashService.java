package com.brewpubs.app.services;

/**
 * Created by Rajiv Shankar on 1/9/26 @ 3:49 PM.
 */

import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * HashService - Password hashing with salt
 *
 * HOW IT WORKS:
 * 1. generateSalt() - Creates random 16-byte salt, encodes as Base64
 * 2. getHashedPassword() - Combines password + salt, runs PBKDF2 algorithm
 *
 * PBKDF2 (Password-Based Key Derivation Function 2):
 * - Industry-standard algorithm for password hashing
 * - Intentionally slow to prevent brute-force attacks
 * - 5000 iterations makes cracking computationally expensive
 */
@Service
public class HashService {

    /**
     * Generate a random salt for a new user
     * Called once during registration, stored in database
     *
     * @return Base64-encoded random salt string
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hash a password with the given salt
     *
     * Used in two scenarios:
     * 1. Registration: Hash the new password with fresh salt
     * 2. Login: Hash the entered password with stored salt, compare to stored hash
     *
     * @param password The plain text password
     * @param salt The salt (Base64 encoded)
     * @return The hashed password (Base64 encoded)
     */
    public String getHashedPassword(String password, String salt) {
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        // PBKDF2 specification: 5000 iterations, 128-bit key
        KeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltBytes,
                5000,  // iterations - makes brute force slow
                128    // key length in bits
        );

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
