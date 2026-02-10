package com.brewpubs.app.services;

import com.brewpubs.app.mappers.NoteMapper;
import com.brewpubs.app.models.Note;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

// ––––– Created by Rajiv Shankar on 2/10/26 @ 11:24 AM ––––– //

/**
 * NoteServiceTest — Unit tests for NoteService using Mockito
 *
 * STRATEGY: Mock the NoteMapper so we test ONLY the service logic,
 * not the database. Fast, isolated, repeatable.
 *
 * PATTERN: Arrange → Act → Assert (AAA)
 */
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteService noteService;

    // ========== CREATE TESTS ==========

    @Test
    void createNote_validInput_returnsOne() {
        // ARRANGE: Mock mapper to return 1 (success)
        when(noteMapper.insert(any(Note.class))).thenReturn(1);

        // ACT: Call service with a Note object
        Note note = new Note("Test Title", "Test Description", 1);
        int result = noteService.createNote(note);

        // ASSERT: Should return 1 (one row inserted)
        assertEquals(1, result);
        verify(noteMapper, times(1)).insert(any(Note.class));
    }

    @Test
    void createNote_validInput_passesCorrectDataToMapper() {
        // ARRANGE
        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        when(noteMapper.insert(any(Note.class))).thenReturn(1);

        // ACT
        Note note = new Note("My Title", "My Description", 42);
        noteService.createNote(note);

        // ASSERT: Verify the exact Note passed to mapper
        verify(noteMapper).insert(captor.capture());
        Note captured = captor.getValue();
        assertEquals("My Title", captured.getTitle());
        assertEquals("My Description", captured.getDescription());
        assertEquals(42, captured.getUserId());
    }

    @Test
    void createNote_insertFails_returnsZero() {
        // ARRANGE: Mock mapper to return 0 (failure)
        when(noteMapper.insert(any(Note.class))).thenReturn(0);

        // ACT
        Note note = new Note("Title", "Desc", 1);
        int result = noteService.createNote(note);

        // ASSERT: Should return 0 (no rows inserted)
        assertEquals(0, result);
    }

    // ========== UPDATE TESTS ==========

    @Test
    void updateNote_validInput_returnsOne() {
        // ARRANGE: Mock mapper to return 1 (success)
        when(noteMapper.update(any(Note.class))).thenReturn(1);

        // ACT: Create Note with all fields including noteId
        Note note = new Note(1, "Updated Title", "Updated Desc", 1);
        int result = noteService.updateNote(note);

        // ASSERT
        assertEquals(1, result);
        verify(noteMapper, times(1)).update(any(Note.class));
    }

    // ========== DELETE TESTS ==========

    @Test
    void deleteNote_validId_returnsOne() {
        // ARRANGE: Mock mapper to return 1 (success)
        when(noteMapper.delete(anyInt())).thenReturn(1);

        // ACT
        int result = noteService.deleteNote(1);

        // ASSERT
        assertEquals(1, result);
        verify(noteMapper, times(1)).delete(eq(1));
    }

    // ========== READ TESTS ==========

    @Test
    void getNotesByUserId_userHasNotes_returnsList() {
        // ARRANGE: Create fake notes
        Note note1 = new Note(1, "Note 1", "Desc 1", 42);
        Note note2 = new Note(2, "Note 2", "Desc 2", 42);
        List<Note> fakeNotes = Arrays.asList(note1, note2);

        when(noteMapper.getNotesByUserId(eq(42))).thenReturn(fakeNotes);

        // ACT
        List<Note> result = noteService.getNotesByUserId(42);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Note 1", result.get(0).getTitle());
        verify(noteMapper, times(1)).getNotesByUserId(eq(42));
    }

    @Test
    void getNotesByUserId_userHasNoNotes_returnsEmptyList() {
        // ARRANGE
        when(noteMapper.getNotesByUserId(anyInt())).thenReturn(Collections.emptyList());

        // ACT
        List<Note> result = noteService.getNotesByUserId(99);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}


