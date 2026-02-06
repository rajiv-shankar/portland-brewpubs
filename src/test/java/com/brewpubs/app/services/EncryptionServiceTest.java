package com.brewpubs.app.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

// ––––– Created by Rajiv Shankar on 2/6/26 @ 3:56 PM ––––– //

/**
 * EncryptionServiceTest — Tests for AES encryption/decryption
 *
 * TESTING STRATEGY:
 * - Verify encrypt → decrypt round-trip recovers original value
 * - Verify each key is unique (SecureRandom)
 * - Verify encrypted value differs from original
 * - Verify same plaintext + different keys → different ciphertext
 * - Verify wrong key fails to decrypt
 */
@SpringBootTest
class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    /**
     * Test 1: Round-trip — encrypt then decrypt returns original
     */
    @Test
    void encryptAndDecrypt_shouldRoundTrip() {
        String original = "MySecret123";
        String key = encryptionService.generateKey();

        String encrypted = encryptionService.encryptValue(original, key);
        String decrypted = encryptionService.decryptValue(encrypted, key);

        assertEquals(original, decrypted, "Round-trip should recover original");
        System.out.println("✅ Original: " + original + " → Decrypted: " + decrypted);
    }

    /**
     * Test 2: Each generated key is unique
     */
    @Test
    void generateKey_shouldReturnUniqueKeys() {
        String key1 = encryptionService.generateKey();
        String key2 = encryptionService.generateKey();

        assertNotEquals(key1, key2, "Keys should be unique");
        System.out.println("✅ Key1: " + key1);
        System.out.println("✅ Key2: " + key2);
    }

    /**
     * Test 3: Encrypted value is different from the original plaintext
     */
    @Test
    void encryptValue_shouldDifferFromOriginal() {
        String original = "PlainTextPassword";
        String key = encryptionService.generateKey();

        String encrypted = encryptionService.encryptValue(original, key);

        assertNotEquals(original, encrypted, "Encrypted value should differ from original");
        assertFalse(encrypted.contains(original), "Encrypted should not contain plaintext");
        System.out.println("✅ Original: " + original + " → Encrypted: " + encrypted);
    }

    /**
     * Test 4: Same plaintext with different keys produces different ciphertext
     */
    @Test
    void encryptValue_differentKeys_shouldProduceDifferentCiphertext() {
        String original = "SamePassword";
        String key1 = encryptionService.generateKey();
        String key2 = encryptionService.generateKey();

        String encrypted1 = encryptionService.encryptValue(original, key1);
        String encrypted2 = encryptionService.encryptValue(original, key2);

        assertNotEquals(encrypted1, encrypted2,
                "Different keys should produce different ciphertext");
        System.out.println("✅ Encrypted1: " + encrypted1);
        System.out.println("✅ Encrypted2: " + encrypted2);
    }

    /**
     * Test 5: Decrypting with the wrong key should throw an exception
     */
    @Test
    void decryptValue_wrongKey_shouldThrow() {
        String original = "SecretValue";
        String correctKey = encryptionService.generateKey();
        String wrongKey   = encryptionService.generateKey();

        String encrypted = encryptionService.encryptValue(original, correctKey);

        // AES with wrong key throws RuntimeException (wrapping javax.crypto exception)
        assertThrows(RuntimeException.class, () -> {
            encryptionService.decryptValue(encrypted, wrongKey);
        }, "Wrong key should cause decryption failure");

        System.out.println("✅ Wrong key correctly throws exception");
    }

    /**
     * Test 6: Round-trip with special characters and unicode
     */
    @Test
    void encryptAndDecrypt_specialChars_shouldRoundTrip() {
        String original = "P@ss!w0rd#$%^&*()_+-=[]{}|;':,.<>?";
        String key = encryptionService.generateKey();

        String encrypted = encryptionService.encryptValue(original, key);
        String decrypted = encryptionService.decryptValue(encrypted, key);

        assertEquals(original, decrypted, "Special chars should survive round-trip");
        System.out.println("✅ Special chars round-trip: " + original);
    }
}


