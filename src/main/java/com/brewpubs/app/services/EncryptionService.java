package com.brewpubs.app.services;

import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

// ––––– Created by Rajiv Shankar on 1/28/26 @ 4:27 PM ––––– //

/**
 * ENCRYPTION SERVICE — Two-Way Encryption/Decryption
 *
 * PURPOSE: Encrypt credential passwords so users can retrieve them later
 *
 * ENCRYPTION vs HASHING:
 * - HASHING: One-way (User login passwords) — HashService
 * - ENCRYPTION: Two-way (Stored credential passwords) — THIS SERVICE
 *
 * FLOW:
 * 1. Generate random encryption key (16 bytes)
 * 2. Encrypt password using key → Store encrypted + key in DB
 * 3. Later: Decrypt password using stored key for user to view/edit
 */
@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES";  // AES symmetric encryption, 128-bit key, ECB mode by default

    /**
     * Generate a random 16-byte encryption key
     * Each credential gets its OWN unique key
     */
    public String generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16]; // 128-bit key
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    /**
     * Encrypt a value using the provided key
     * @param value - Plain text to encrypt (e.g., "myPassword123")
     * @param key - Base64-encoded encryption key
     * @return Base64-encoded encrypted value
     */
    public String encryptValue(String value, String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt a value using the provided key
     * @param encryptedValue - Base64-encoded encrypted value
     * @param key - Base64-encoded encryption key
     * @return Plain text original value
     */
    public String decryptValue(String encryptedValue, String key) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(key);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}


