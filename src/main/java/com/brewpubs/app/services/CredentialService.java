package com.brewpubs.app.services;

import com.brewpubs.app.mappers.CredentialMapper;
import com.brewpubs.app.models.Credential;
import org.springframework.stereotype.Service;
import java.util.List;

// ––––– Created by Rajiv Shankar on 1/30/26 @ 10:18 AM ––––– //

/**
 * CREDENTIAL SERVICE — Business logic for credential management
 *
 * ENCRYPTION FLOW:
 * CREATE:
 *   1. User enters plain password in form
 *   2. generateKey() → random encryption key
 *   3. encryptValue(password, key) → encrypted password
 *   4. Store encrypted password + key in database
 *
 * DISPLAY LIST:
 *   - Show encrypted password (user can't read it)
 *
 * EDIT MODAL:
 *   - Call getCredentialWithDecryption()
 *   - decryptValue(encrypted, key) → plain password
 *   - Show plain password in edit form
 */
@Service
public class CredentialService {

    private final CredentialMapper credentialMapper;
    private final EncryptionService encryptionService;

    public CredentialService(CredentialMapper credentialMapper,
                             EncryptionService encryptionService) {
        this.credentialMapper = credentialMapper;
        this.encryptionService = encryptionService;
    }

    /**
     * Get all credentials for user (passwords shown ENCRYPTED)
     */
    public List<Credential> getCredentialsByUserId(Integer userId) {
        return credentialMapper.findByUserId(userId);
    }

    /**
     * Get credential WITHOUT DECRYPTION by Id.
     * Used for ownership verification before decrypting.
     */
    public Credential getCredentialById(Integer credentialId) {
        return credentialMapper.findById(credentialId);
    }

    /**
     * Get credential WITH DECRYPTION for editing
     * This is called when user clicks Edit button
     */
    public Credential getCredentialWithDecryption(Integer credentialId) {
        Credential credential = credentialMapper.findById(credentialId);
        if (credential != null) {
            String decrypted = encryptionService.decryptValue(
                    credential.getPassword(),
                    credential.getEncryptionKey()
            );
            credential.setPassword(decrypted); // Replace encrypted with plain
        }
        return credential;
    }

    /**
     * Create new credential
     * Flow: Generate key → Encrypt password → Store both
     */
    public String createCredential(String url, String username, String plainPassword,
                                   Integer userId) {
        try {
            // Generate encryption key for this credential
            String key = encryptionService.generateKey();

            // Encrypt the password
            String encryptedPassword = encryptionService.encryptValue(plainPassword, key);

            // Create credential with encrypted data
            Credential credential = new Credential(url, username, key, encryptedPassword, userId);

            // Insert into database
            credentialMapper.insert(credential);

            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Update existing credential
     * Flow: Generate NEW key → Encrypt password → Update both
     */
    public String updateCredential(Integer credentialId, String url, String username,
                                   String plainPassword, Integer userId) {
        try {
            // Get existing credential to verify ownership
            Credential existing = credentialMapper.findById(credentialId);
            if (existing == null || !existing.getUserId().equals(userId)) {
                return "unauthorized";
            }

            // Generate NEW key and encrypt password
            String newKey = encryptionService.generateKey();
            String encryptedPassword = encryptionService.encryptValue(plainPassword, newKey);

            // Update credential
            existing.setUrl(url);
            existing.setUsername(username);
            existing.setEncryptionKey(newKey);
            existing.setPassword(encryptedPassword);

            credentialMapper.update(existing);

            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    /**
     * Delete credential
     */
    public boolean deleteCredential(Integer credentialId, Integer userId) {
        // Security: verify ownership before deletion
        Credential credential = credentialMapper.findById(credentialId);
        if (credential == null || !credential.getUserId().equals(userId)) {
            return false;
        }

        return credentialMapper.deleteById(credentialId) > 0;
    }
}

