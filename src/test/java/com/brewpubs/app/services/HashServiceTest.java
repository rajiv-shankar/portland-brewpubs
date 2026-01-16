package com.brewpubs.app.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HashServiceTest - Tests for password hashing functionality
 *
 * TESTING STRATEGY:
 * - Verify salt generation produces unique values
 * - Verify same password + same salt = same hash
 * - Verify different salts = different hashes
 * - Verify hash is consistent (deterministic)
 *
 * WHY TEST THIS:
 * HashService is critical for security. If hashing breaks, passwords are compromised.
 */
@SpringBootTest  // Loads Spring context to inject HashService
class HashServiceTest {

    @Autowired
    private HashService hashService;

    /**
     * Test 1: Salt generation produces non-null, non-empty strings
     */
    @Test
    void generateSalt_shouldReturnNonEmptyString() {
        // Act: Generate salt
        String salt = hashService.generateSalt();

        // Assert: Salt is not null and has content
        assertNotNull(salt, "Salt should not be null");
        assertFalse(salt.isEmpty(), "Salt should not be empty");

        System.out.println("✅ Generated salt: " + salt);
    }

    /**
     * Test 2: Each salt should be unique
     */
    @Test
    void generateSalt_shouldReturnUniqueSalts() {
        // Act: Generate two salts
        String salt1 = hashService.generateSalt();
        String salt2 = hashService.generateSalt();

        // Assert: They should be different
        assertNotEquals(salt1, salt2, "Each salt should be unique");

        System.out.println("✅ Salt 1: " + salt1);
        System.out.println("✅ Salt 2: " + salt2);
    }

    /**
     * Test 3: Same password + same salt should produce consistent hash
     */
    @Test
    void getHashedPassword_shouldBeConsistent() {
        // Arrange
        String password = "testPassword123";
        String salt = hashService.generateSalt();

        // Act: Hash same password twice with same salt
        String hash1 = hashService.getHashedPassword(password, salt);
        String hash2 = hashService.getHashedPassword(password, salt);

        // Assert: Hashes should be identical
        assertEquals(hash1, hash2, "Same password + same salt should produce same hash");

        System.out.println("✅ Consistent hash: " + hash1);
    }

    /**
     * Test 4: Different salts should produce different hashes
     */
    @Test
    void getHashedPassword_shouldProduceDifferentHashesWithDifferentSalts() {
        // Arrange
        String password = "testPassword123";
        String salt1 = hashService.generateSalt();
        String salt2 = hashService.generateSalt();

        // Act: Hash same password with different salts
        String hash1 = hashService.getHashedPassword(password, salt1);
        String hash2 = hashService.getHashedPassword(password, salt2);

        // Assert: Hashes should be different
        assertNotEquals(hash1, hash2, "Different salts should produce different hashes");

        System.out.println("✅ Hash with salt1: " + hash1);
        System.out.println("✅ Hash with salt2: " + hash2);
    }

    /**
     * Test 5: Hash should not contain original password
     */
    @Test
    void getHashedPassword_shouldNotContainOriginalPassword() {
        // Arrange
        String password = "mySecretPassword";
        String salt = hashService.generateSalt();

        // Act
        String hash = hashService.getHashedPassword(password, salt);

        // Assert: Hash should not contain the password text
        assertFalse(hash.contains(password), "Hash should not contain original password");

        System.out.println("✅ Password: " + password);
        System.out.println("✅ Hash: " + hash);
    }
}


