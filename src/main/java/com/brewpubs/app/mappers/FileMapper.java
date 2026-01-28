package com.brewpubs.app.mappers;

import com.brewpubs.app.models.File;
import org.apache.ibatis.annotations.*;
import java.util.List;

// ––––– Created by Rajiv Shankar on 1/27/26 @ 4:35 PM ––––– //

@Mapper
public interface FileMapper {

    // Get all files for a user
    @Select("SELECT file_id, filename, content_type, " +
            "file_data, user_id FROM FILES " +
            "WHERE user_id = #{userId}")
    List<File> findByUserId(int userId);  // `File` contains `byte[]` (byte array) field to store binary data = BLOBs in SQL

    // Get specific file by ID
    @Select("SELECT file_id, filename, content_type, " +
            "file_data, user_id FROM FILES " +
            "WHERE file_id = #{fileId}")
    File findById(int fileId);

    // Check for duplicate filename
    @Select("SELECT file_id, filename, content_type, " +
            "file_data, user_id FROM FILES " +
            "WHERE filename = #{filename} " +
            "AND user_id = #{userId}")
    File findByFilenameAndUserId(
            @Param("filename") String filename,
            @Param("userId") int userId);

    // Insert new file
    @Insert("INSERT INTO FILES " +
            "(filename, content_type, file_data, user_id) " +
            "VALUES (#{filename}, #{contentType}, " +
            "#{fileData}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "fileId")
    int insert(File file);

    // Delete file
    @Delete("DELETE FROM FILES WHERE file_id = #{fileId}")
    int deleteById(int fileId);
}

