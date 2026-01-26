package com.brewpubs.app.mappers;

import com.brewpubs.app.models.Note;
import org.apache.ibatis.annotations.*;
import java.util.List;

// Created by Rajiv Shankar on 1/23/26 @ 2:58 PM //

/**
 * NoteMapper - MyBatis interface for NOTES table operations
 *
 * USER DATA ISOLATION:
 * - Every query filters by user_id to ensure users see only their own notes
 * - Never use queries like "SELECT * FROM NOTES" without WHERE user_id = #{userId}
 *
 * CRUD OPERATIONS:
 * - Create: insert(Note) with auto-generated noteId
 * - Read: getNotesByUserId(userId) - filtered by user
 * - Update: update(Note) - requires noteId
 * - Delete: delete(noteId) - no userId check needed (caller verifies ownership)
 */
@Mapper
public interface NoteMapper {

    // ========== CREATE OPERATION ==========

    /**
     * Insert a new note into database
     * @param note The Note object to insert (must have userId set)
     * @return Number of rows inserted (should be 1)
     *
     * AUTO-GENERATED KEY:
     * - @Options tells MyBatis to retrieve auto-generated note_id from database
     * - After insert, note.getNoteId() will return the generated ID
     */
    @Insert("INSERT INTO NOTES (title, description, user_id) " +
            "VALUES (#{title}, #{description}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "noteId")
    int insert(Note note);

    // ========== READ OPERATIONS ==========

    /**
     * Get all notes for a specific user
     * @param userId The user ID to filter by
     * @return List of all notes belonging to this user
     *
     * USER ISOLATION: Only returns notes where user_id matches parameter
     */
    @Select("SELECT * FROM NOTES WHERE user_id = #{userId}")
    List<Note> getNotesByUserId(Integer userId);

    /**
     * Get a single note by its ID
     * @param noteId The note ID to search for
     * @return Note if found, null if not found
     *
     * NOTE: This does NOT filter by userId - caller must verify ownership
     */
    @Select("SELECT * FROM NOTES WHERE note_id = #{noteId}")
    Note getNoteById(Integer noteId);

    // ========== UPDATE OPERATION ==========

    /**
     * Update an existing note's title and description
     * @param note Note object with updated values (must have noteId set)
     * @return Number of rows updated (should be 1 if note exists)
     *
     * SECURITY: Does not allow changing userId (prevents note theft)
     */
    @Update("UPDATE NOTES SET " +
            "title = #{title}, " +
            "description = #{description} " +
            "WHERE note_id = #{noteId}")
    int update(Note note);

    // ========== DELETE OPERATION ==========

    /**
     * Delete a note by its ID
     * @param noteId The ID of note to delete
     * @return Number of rows deleted (should be 1 if note exists)
     *
     * NOTE: Caller must verify user owns this note before calling delete
     */
    @Delete("DELETE FROM NOTES WHERE note_id = #{noteId}")
    int delete(Integer noteId);
}
