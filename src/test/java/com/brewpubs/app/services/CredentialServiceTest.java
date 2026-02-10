package com.brewpubs.app.services;

import com.brewpubs.app.mappers.CredentialMapper;
import com.brewpubs.app.models.Credential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ––––– Created by Rajiv Shankar on 2/10/26 @ 2:22 PM ––––– //

/**
 * CredentialServiceTest — Unit tests for CredentialService using Mockito
 *
 * KEY LEARNING: CredentialService has TWO mocked dependencies:
 * - CredentialMapper (database access)
 * - EncryptionService (encrypt/decrypt passwords)
 *
 * This tests the full encryption flow WITHOUT real encryption or a database.
 */
@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {

    @Mock
    private CredentialMapper credentialMapper;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private CredentialService credentialService;

    // ========== CREATE TESTS ==========

    @Test
    void createCredential_validInput_encryptsAndInserts() {
        // ARRANGE: Mock encryption flow
        when(encryptionService.generateKey()).thenReturn("fakeKey123");
        when(encryptionService.encryptValue(eq("myPass"), eq("fakeKey123")))
                .thenReturn("encryptedXYZ");
        when(credentialMapper.insert(any(Credential.class))).thenReturn(1);

        // ACT
        String result = credentialService.createCredential(
                "https://github.com", "user", "myPass", 1);

        // ASSERT
        assertEquals("success", result);
        verify(encryptionService, times(1)).generateKey();
        verify(encryptionService, times(1)).encryptValue("myPass", "fakeKey123");
        verify(credentialMapper, times(1)).insert(any(Credential.class));
    }

    @Test
    void createCredential_validInput_passesEncryptedDataToMapper() {
        // ARRANGE
        ArgumentCaptor<Credential> captor = ArgumentCaptor.forClass(Credential.class);
        when(encryptionService.generateKey()).thenReturn("testKey");
        when(encryptionService.encryptValue(anyString(), anyString())).thenReturn("encrypted123");
        when(credentialMapper.insert(any(Credential.class))).thenReturn(1);

        // ACT
        credentialService.createCredential("https://example.com", "user1", "plainPass", 42);

        // ASSERT: Verify encrypted data was passed to mapper
        verify(credentialMapper).insert(captor.capture());
        Credential captured = captor.getValue();
        assertEquals("https://example.com", captured.getUrl());
        assertEquals("user1", captured.getUsername());
        assertEquals("encrypted123", captured.getPassword());         // Encrypted, NOT plain!
        assertEquals("testKey", captured.getEncryptionKey());          // Key stored alongside
        assertEquals(42, captured.getUserId());
    }

    // ========== UPDATE TESTS ==========

    @Test
    void updateCredential_validOwner_generatesNewKeyAndUpdates() {
        // ARRANGE: Existing credential belongs to user 1
        Credential existing = new Credential();
        existing.setCredentialId(5);
        existing.setUserId(1);

        when(credentialMapper.findById(eq(5))).thenReturn(existing);
        when(encryptionService.generateKey()).thenReturn("newKey456");
        when(encryptionService.encryptValue(eq("updatedPass"), eq("newKey456")))
                .thenReturn("newEncrypted789");
        when(credentialMapper.update(any(Credential.class))).thenReturn(1);

        // ACT
        String result = credentialService.updateCredential(
                5, "https://updated.com", "updatedUser", "updatedPass", 1);

        // ASSERT
        assertEquals("success", result);
        verify(encryptionService, times(1)).generateKey();
        verify(encryptionService, times(1)).encryptValue("updatedPass", "newKey456");
        verify(credentialMapper, times(1)).update(any(Credential.class));
    }

    @Test
    void updateCredential_wrongOwner_returnsUnauthorized() {
        // ARRANGE: Credential belongs to user 1, but user 99 tries to update
        Credential existing = new Credential();
        existing.setCredentialId(5);
        existing.setUserId(1);

        when(credentialMapper.findById(eq(5))).thenReturn(existing);

        // ACT: User 99 attempts update
        String result = credentialService.updateCredential(
                5, "https://hack.com", "hacker", "stolen", 99);

        // ASSERT: Should be denied
        assertEquals("unauthorized", result);
        verify(credentialMapper, never()).update(any(Credential.class));
    }

    // ========== DELETE TESTS ==========

    @Test
    void deleteCredential_validOwner_returnsTrue() {
        // ARRANGE: Credential exists and belongs to user 1
        Credential credential = new Credential();
        credential.setCredentialId(10);
        credential.setUserId(1);

        when(credentialMapper.findById(eq(10))).thenReturn(credential);
        when(credentialMapper.deleteById(eq(10))).thenReturn(1);

        // ACT
        boolean result = credentialService.deleteCredential(10, 1);

        // ASSERT
        assertTrue(result);
        verify(credentialMapper, times(1)).deleteById(eq(10));
    }

    @Test
    void deleteCredential_wrongOwner_returnsFalse() {
        // ARRANGE: Credential belongs to user 1
        Credential credential = new Credential();
        credential.setCredentialId(10);
        credential.setUserId(1);

        when(credentialMapper.findById(eq(10))).thenReturn(credential);

        // ACT: User 99 attempts deletion
        boolean result = credentialService.deleteCredential(10, 99);

        // ASSERT
        assertFalse(result);
        verify(credentialMapper, never()).deleteById(anyInt());
    }

    // ========== READ TESTS ==========

    @Test
    void getCredentialWithDecryption_validId_decryptsPassword() {
        // ARRANGE: Credential with encrypted password
        Credential encryptedCred = new Credential();
        encryptedCred.setCredentialId(7);
        encryptedCred.setUrl("https://secure.com");
        encryptedCred.setPassword("encryptedXYZ");
        encryptedCred.setEncryptionKey("secretKey");

        when(credentialMapper.findById(eq(7))).thenReturn(encryptedCred);
        when(encryptionService.decryptValue(eq("encryptedXYZ"), eq("secretKey")))
                .thenReturn("decryptedPassword");

        // ACT
        Credential result = credentialService.getCredentialWithDecryption(7);

        // ASSERT: Password should now be decrypted
        assertNotNull(result);
        assertEquals("decryptedPassword", result.getPassword());
        verify(encryptionService, times(1)).decryptValue("encryptedXYZ", "secretKey");
    }

    @Test
    void getCredentialsByUserId_userHasCredentials_returnsList() {
        // ARRANGE
        Credential cred1 = new Credential();
        cred1.setCredentialId(1);
        cred1.setPassword("encrypted1");

        Credential cred2 = new Credential();
        cred2.setCredentialId(2);
        cred2.setPassword("encrypted2");

        List<Credential> fakeCreds = Arrays.asList(cred1, cred2);
        when(credentialMapper.findByUserId(eq(42))).thenReturn(fakeCreds);

        // ACT
        List<Credential> result = credentialService.getCredentialsByUserId(42);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("encrypted1", result.get(0).getPassword());
    }

    @Test
    void getCredentialById_validId_returnsWithoutDecryption() {
        // ARRANGE
        Credential fakeCred = new Credential();
        fakeCred.setCredentialId(15);
        fakeCred.setPassword("stillEncrypted");
        fakeCred.setUserId(99);

        when(credentialMapper.findById(eq(15))).thenReturn(fakeCred);

        // ACT
        Credential result = credentialService.getCredentialById(15);

        // ASSERT: Password should still be encrypted (no decryption called)
        assertNotNull(result);
        assertEquals("stillEncrypted", result.getPassword());
        verify(encryptionService, never()).decryptValue(anyString(), anyString());
    }
}

