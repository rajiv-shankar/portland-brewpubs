package com.brewpubs.app.services;

import com.brewpubs.app.mappers.FileMapper;
import com.brewpubs.app.models.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

// ––––– Created by Rajiv Shankar on 1/27/26 @ 5:39 PM ––––– //

@Service
public class FileService {

    private final FileMapper fileMapper;
    // Logger: app's "flight recorder"; logs app behavior, warnings, execution errors; helps debug production issues; routes output to files, databases, monitoring systems
    private final Logger logger = LoggerFactory.getLogger(FileService.class);

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
        logger.info("✅ FileService initialized");
    }

    // UPLOAD file
    /* MultipartFile: represents a file uploaded as part of an HTTP request; typically from an HTML <form> w `enctype="multipart/form-data"`;
     but not the file itself; encapsulates: file’s contents, filename, content type, metadata (like size), methods (to read or save the file);
     stored in memory (or a temp location) (transient) */
    public String uploadFile(MultipartFile multipartFile, int userId) {
        try {
            String filename = multipartFile.getOriginalFilename();
            String contentType = multipartFile.getContentType();
            byte[] fileData = multipartFile.getBytes();

            logger.info("📤 Uploading: {} ({})", filename, contentType);

            // Check if empty
            if (multipartFile.isEmpty()) {
                return "error_empty";
            }

            // Check for duplicate
            if (fileMapper.findByFilenameAndUserId(filename, userId) != null) {
                return "error_duplicate";
            }

            // Create File model (see File.java)
            File file = new File();
            file.setFilename(filename);
            file.setContentType(contentType);
            file.setFileData(fileData);
            file.setUserId(userId);

            // Save to database
            int result = fileMapper.insert(file);
            return result > 0 ? "success" : "error_database";

        } catch (IOException e) {
            logger.error("❌ IOException", e);
            return "error_io";
        }
    }

    // Get all files for user
    public List<File> getFilesByUserId(int userId) {
        return fileMapper.findByUserId(userId);
    }

    // Get specific file
    public File getFileById(int fileId) {
        return fileMapper.findById(fileId);
    }

    // DELETE file (with ownership check)
    public boolean deleteFile(int fileId, int userId) {
        File file = fileMapper.findById(fileId);
        if (file == null || file.getUserId() != userId) {
            return false;
        }
        return fileMapper.deleteById(fileId) > 0;
    }
}

