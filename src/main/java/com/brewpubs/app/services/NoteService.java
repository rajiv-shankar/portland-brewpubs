package com.brewpubs.app.services;

import com.brewpubs.app.mappers.NoteMapper;
import com.brewpubs.app.models.Note;
import org.springframework.stereotype.Service;
import java.util.List;

// ––––– Created by Rajiv Shankar on 1/26/26 @ 2:07 PM ––––– //

/**
 * NoteService - Business logic for note operations
 *
 * RESPONSIBILITY:
 * - Handles business rules (validation, authorization)
 * - Delegates data access to NoteMapper
 * - Provides clean API for controllers
 *
 * PATTERN:
 * Controller → Service → Mapper → Database
 *
 * WHY SERVICE LAYER:
 * - Separates business logic from HTTP handling
 * - Reusable across multiple controllers
 * - Easier to test (can mock NoteMapper)
 * - Can add validation, logging, transactions here
 */
@Service
public class NoteService {

    private final NoteMapper noteMapper;

    // Constructor injection - Spring provides NoteMapper automatically
    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
        System.out.println("✅ NoteService initialized with NoteMapper");
    }

    // ========== CREATE OPERATION ==========

    /**
     * Create a new note for a user
     * @param note Note object with title, description, userId
     * @return Number of rows inserted (1 if successful)
     *
     * VALIDATION: Title max 20 chars, description max 1000 chars
     * (Enforced by database schema, could add Java validation here)
     */
    public int createNote(Note note) {
        System.out.println("📝 Creating note: " + note.getTitle() + " for user " + note.getUserId());
        return noteMapper.insert(note);
    }

    // ========== READ OPERATIONS ==========

    /**
     * Get all notes for a specific user
     * @param userId The user ID to filter by
     * @return List of notes (empty list if none)
     */
    public List<Note> getNotesByUserId(Integer userId) {
        System.out.println("🔍 Fetching notes for user " + userId);
        return noteMapper.getNotesByUserId(userId);
    }

    /**
     * Get a single note by ID
     * @param noteId The note ID
     * @return Note if found, null if not found
     */
    public Note getNoteById(Integer noteId) {
        return noteMapper.getNoteById(noteId);
    }

    // ========== UPDATE OPERATION ==========

    /**
     * Update an existing note's content
     * @param note Note object with updated title and description
     * @return Number of rows updated (1 if successful)
     *
     * SECURITY: Caller must verify user owns this note before calling
     */
    public int updateNote(Note note) {
        System.out.println("✏️ Updating note " + note.getNoteId());
        return noteMapper.update(note);
    }

    // ========== DELETE OPERATION ==========

    /**
     * Delete a note by ID
     * @param noteId The ID of note to delete
     * @return Number of rows deleted (1 if successful)
     *
     * SECURITY: Caller must verify user owns this note before calling
     */
    public int deleteNote(Integer noteId) {
        System.out.println("🗑️ Deleting note " + noteId);
        return noteMapper.delete(noteId);
    }
}

