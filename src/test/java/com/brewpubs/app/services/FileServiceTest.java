package com.brewpubs.app.services;

import com.brewpubs.app.mappers.FileMapper;
import com.brewpubs.app.models.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ––––– Created by Rajiv Shankar on 2/10/26 @ 2:20 PM ––––– //

/**
 * FileServiceTest — Unit tests for FileService using Mockito
 *
 * KEY LEARNING: FileService.uploadFile() takes a MultipartFile (Spring's
 * wrapper for HTTP file uploads), so we must MOCK MultipartFile too!
 * This is a great example of Mockito's power — we can fake an entire
 * HTTP file upload without running a web server.
 */
@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private MultipartFile multipartFile;  // Mock the uploaded file too!

    @InjectMocks
    private FileService fileService;

    // ========== UPLOAD TESTS ==========

    @Test
    void uploadFile_validFile_returnsSuccess() throws IOException {
        // ARRANGE: Mock MultipartFile behavior
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn("test content".getBytes());
        when(multipartFile.isEmpty()).thenReturn(false);

        // Mock mapper: no duplicate, insert succeeds
        when(fileMapper.findByFilenameAndUserId(anyString(), anyInt())).thenReturn(null);
        when(fileMapper.insert(any(File.class))).thenReturn(1);

        // ACT
        String result = fileService.uploadFile(multipartFile, 1);

        // ASSERT
        assertEquals("success", result);
        verify(fileMapper, times(1)).insert(any(File.class));
    }

    @Test
    void uploadFile_validFile_passesCorrectDataToMapper() throws IOException {
        // ARRANGE
        ArgumentCaptor<File> captor = ArgumentCaptor.forClass(File.class);
        when(multipartFile.getOriginalFilename()).thenReturn("report.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getBytes()).thenReturn("pdf content".getBytes());
        when(multipartFile.isEmpty()).thenReturn(false);

        when(fileMapper.findByFilenameAndUserId(anyString(), anyInt())).thenReturn(null);
        when(fileMapper.insert(any(File.class))).thenReturn(1);

        // ACT
        fileService.uploadFile(multipartFile, 42);

        // ASSERT: Verify the File object passed to mapper
        verify(fileMapper).insert(captor.capture());
        File captured = captor.getValue();
        assertEquals("report.pdf", captured.getFilename());
        assertEquals("application/pdf", captured.getContentType());
        assertEquals(42, captured.getUserId());
        assertNotNull(captured.getFileData());
    }

    @Test
    void uploadFile_emptyFile_returnsErrorEmpty() throws IOException {
        // ARRANGE: Mock an empty file
        when(multipartFile.getOriginalFilename()).thenReturn("empty.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn(new byte[0]);
        when(multipartFile.isEmpty()).thenReturn(true);

        // ACT
        String result = fileService.uploadFile(multipartFile, 1);

        // ASSERT
        assertEquals("error_empty", result);
        verify(fileMapper, never()).insert(any(File.class));  // Should NOT attempt insert
    }

    @Test
    void uploadFile_duplicateFilename_returnsErrorDuplicate() throws IOException {
        // ARRANGE: Mock a duplicate file scenario
        when(multipartFile.getOriginalFilename()).thenReturn("existing.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.isEmpty()).thenReturn(false);

        // Mock mapper: duplicate found!
        File existingFile = new File();
        existingFile.setFilename("existing.txt");
        when(fileMapper.findByFilenameAndUserId(eq("existing.txt"), eq(1))).thenReturn(existingFile);

        // ACT
        String result = fileService.uploadFile(multipartFile, 1);

        // ASSERT
        assertEquals("error_duplicate", result);
        verify(fileMapper, never()).insert(any(File.class));
    }

    @Test
    void uploadFile_insertFails_returnsErrorDatabase() throws IOException {
        // ARRANGE
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        when(multipartFile.getContentType()).thenReturn("text/plain");
        when(multipartFile.getBytes()).thenReturn("content".getBytes());
        when(multipartFile.isEmpty()).thenReturn(false);

        when(fileMapper.findByFilenameAndUserId(anyString(), anyInt())).thenReturn(null);
        when(fileMapper.insert(any(File.class))).thenReturn(0);  // Insert fails

        // ACT
        String result = fileService.uploadFile(multipartFile, 1);

        // ASSERT
        assertEquals("error_database", result);
    }

    // ========== DELETE TESTS ==========

    @Test
    void deleteFile_validOwner_returnsTrue() {
        // ARRANGE: File exists and belongs to user
        File file = new File();
        file.setFileId(1);
        file.setUserId(42);

        when(fileMapper.findById(eq(1))).thenReturn(file);
        when(fileMapper.deleteById(eq(1))).thenReturn(1);

        // ACT
        boolean result = fileService.deleteFile(1, 42);

        // ASSERT
        assertTrue(result);
        verify(fileMapper, times(1)).deleteById(eq(1));
    }

    @Test
    void deleteFile_wrongOwner_returnsFalse() {
        // ARRANGE: File belongs to user 42, but user 99 tries to delete
        File file = new File();
        file.setFileId(1);
        file.setUserId(42);

        when(fileMapper.findById(eq(1))).thenReturn(file);

        // ACT: User 99 attempts deletion
        boolean result = fileService.deleteFile(1, 99);

        // ASSERT: Should be denied
        assertFalse(result);
        verify(fileMapper, never()).deleteById(anyInt());
    }
}


