package com.brewpubs.app.controllers;

import com.brewpubs.app.models.Note;
import com.brewpubs.app.models.User;
import com.brewpubs.app.services.BreweryService;
import com.brewpubs.app.services.NoteService;
import com.brewpubs.app.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// ––––– Created by Rajiv Shankar on 11/13/25 @ 8:06 PM ––––– //

/**
 * Controller: receives & processes incoming HTTP requests | returns appropriate views (HTML pages) to users |
 * key component in Spring's Model-View-Controller (MVC) architectural pattern |
 * HomeController manages requests to homepage
 */
@Controller
public class HomeController {

    // Field declaration: "shelf" to store (reserves a spot) the dependency for use throughout this class
    private final BreweryService breweryService;
    private final UserService userService;
    private final NoteService noteService;

    // Constructor injection - Spring provides both services
    public HomeController(BreweryService breweryService, UserService userService, NoteService noteService) {
        this.breweryService = breweryService;
        this.userService = userService;
        this.noteService = noteService;
        System.out.println("✅ HomeController initialized with BreweryService, UserService, and NoteService");
    }

    /**
     * GET / - Homepage
     *
     * NEW: Principal parameter
     * - If user is logged in, Principal is NOT null → principal.getName() returns username
     * - If user is anonymous, Principal IS null → show generic greeting
     *
     * FLOW:
     * 1. Check if Principal is null (anonymous user)
     * 2. If logged in, fetch User from database by username
     * 3. Add firstName to model for personalized greeting
     * 4. Template uses th:if to show appropriate message
     */
    @GetMapping("/")
    public String home(Model model, Principal principal) {

        // Standard attributes for all users
        model.addAttribute("cityName", "Portland");
        model.addAttribute("stateName", "Maine");
        model.addAttribute("currentPage", "home");
        model.addAttribute("breweryCount", breweryService.getBreweryCount());

        // NEW: User-specific attributes
        if (principal != null) {
            // User is logged in
            String username = principal.getName();
            System.out.println("🔐 Authenticated user visiting home: " + username);

            // Fetch full User object from database
            User user = userService.getUser(username);

            if (user != null && user.getFirstName() != null && !user.getFirstName().isEmpty()) {  // User has firstName
                // User has firstName set → personalized greeting
                model.addAttribute("userFirstName", user.getFirstName());
                model.addAttribute("isAuthenticated", true);
            } else {
                // User exists but no firstName → use username
                model.addAttribute("userFirstName", username);
                model.addAttribute("isAuthenticated", true);
            }
        } else {
            // Anonymous user
            System.out.println("👤 Anonymous user visiting home");
            model.addAttribute("isAuthenticated", false);
        }

        // NEW: Load user's notes for Notes tab
        if (principal != null) {
            User user = userService.getUser(principal.getName());
            List<Note> notes = noteService.getNotesByUserId(user.getUserId());
            model.addAttribute("notes", notes);
            System.out.println("📋 Loaded " + notes.size() + " notes for user " + user.getUsername());
        } else {
            model.addAttribute("notes", new ArrayList<>());  // Empty list for anonymous users
        }

        // Add empty Note object for form binding
        model.addAttribute("note", new Note());

        return "home";
    }

    // ========== NOTE CRUD ENDPOINTS ==========

    /**
     * POST /notes - Create new note
     *
     * FLOW:
     * 1. User submits form (title + description)
     * 2. Spring binds form data to Note object via @ModelAttribute
     * 3. Get logged-in user from Principal
     * 4. Set note's userId to logged-in user's ID
     * 5. Save note to database via NoteService
     * 6. Redirect back to home page (which will reload notes list)
     */
    @PostMapping("/notes")
    public String createNote(@ModelAttribute Note note, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";  // Not logged in
        }

        try {
            // Get logged-in user
            User user = userService.getUser(principal.getName());

            // Set foreign key (which user owns this note)
            note.setUserId(user.getUserId());

            // Save to database
            int rowsInserted = noteService.createNote(note);

            if (rowsInserted > 0) {
                System.out.println("✅ Note created successfully");
                redirectAttributes.addFlashAttribute("noteSuccess", "Note added successfully!");
            }
        } catch (Exception e) {
            System.out.println("❌ Error creating note: " + e.getMessage());
            redirectAttributes.addFlashAttribute("noteError", "Failed to create note. Please try again.");
        }

        return "redirect:/";
    }

    /**
     * POST /notes/update - Update existing note
     *
     * SECURITY:
     * - Verify logged-in user owns the note before updating
     * - Don't allow changing userId (prevents note theft)
     */
    @PostMapping("/notes/update")
    public String updateNote(@ModelAttribute Note note, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // Get logged-in user
            User user = userService.getUser(principal.getName());

            // Verify user owns this note
            Note existingNote = noteService.getNoteById(note.getNoteId());
            if (existingNote == null) {
                redirectAttributes.addFlashAttribute("noteError", "Note not found.");
                return "redirect:/";
            }

            if (!existingNote.getUserId().equals(user.getUserId())) {
                redirectAttributes.addFlashAttribute("noteError", "You don't have permission to edit this note.");
                return "redirect:/";
            }

            // Update note (userId cannot be changed)
            int rowsUpdated = noteService.updateNote(note);

            if (rowsUpdated > 0) {
                System.out.println("✅ Note updated successfully");
                redirectAttributes.addFlashAttribute("noteSuccess", "Note updated successfully!");
            }
        } catch (Exception e) {
            System.out.println("❌ Error updating note: " + e.getMessage());
            redirectAttributes.addFlashAttribute("noteError", "Failed to update note. Please try again.");
        }

        return "redirect:/";
    }

    /**
     * GET /notes/delete/{id} - Delete note by ID
     *
     * SECURITY:
     * - Verify logged-in user owns the note before deleting
     *
     * PATH VARIABLE:
     * - {id} in URL becomes method parameter
     * - Example: GET /notes/delete/5 → deleteNote(5, ...)
     */
    @GetMapping("/notes/delete/{id}")
    public String deleteNote(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            // Get logged-in user
            User user = userService.getUser(principal.getName());

            // Verify user owns this note
            Note note = noteService.getNoteById(id);
            if (note == null) {
                redirectAttributes.addFlashAttribute("noteError", "Note not found.");
                return "redirect:/";
            }

            if (!note.getUserId().equals(user.getUserId())) {
                redirectAttributes.addFlashAttribute("noteError", "You don't have permission to delete this note.");
                return "redirect:/";
            }

            // Delete note
            int rowsDeleted = noteService.deleteNote(id);

            if (rowsDeleted > 0) {
                System.out.println("✅ Note deleted successfully");
                redirectAttributes.addFlashAttribute("noteSuccess", "Note deleted successfully!");
            }
        } catch (Exception e) {
            System.out.println("❌ Error deleting note: " + e.getMessage());
            redirectAttributes.addFlashAttribute("noteError", "Failed to delete note. Please try again.");
        }

        return "redirect:/";
    }

}
