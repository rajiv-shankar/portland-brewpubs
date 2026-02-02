package com.brewpubs.app.mappers;

import com.brewpubs.app.models.Credential;
import org.apache.ibatis.annotations.*;
import java.util.List;

// ––––– Created by Rajiv Shankar on 1/30/26 @ 10:05 AM ––––– //
/**
 * CREDENTIAL MAPPER — Database operations for CREDENTIALS table
 *
 * PATTERN: Same as NoteMapper/FileMapper
 * 1. findByUserId() — Get all credentials for logged-in user
 * 2. findById() — Get single credential for viewing/editing
 * 3. insert() — Store new credential with encrypted password
 * 4. update() — Update credential with re-encrypted password
 * 5. deleteById() — Remove credential
 */
@Mapper
public interface CredentialMapper {

    @Select("SELECT * FROM CREDENTIALS WHERE user_id = #{userId}")
    List<Credential> findByUserId(Integer userId);

    @Select("SELECT * FROM CREDENTIALS WHERE credential_id = #{credentialId}")
    Credential findById(Integer credentialId);

    @Insert("INSERT INTO CREDENTIALS (url, username, encryption_key, password, user_id) " +
            "VALUES (#{url}, #{username}, #{encryptionKey}, #{password}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "credentialId")
    int insert(Credential credential);

    @Update("UPDATE CREDENTIALS SET url = #{url}, username = #{username}, " +
            "encryption_key = #{encryptionKey}, password = #{password} " +
            "WHERE credential_id = #{credentialId}")
    int update(Credential credential);

    @Delete("DELETE FROM CREDENTIALS WHERE credential_id = #{credentialId}")
    int deleteById(Integer credentialId);
}

