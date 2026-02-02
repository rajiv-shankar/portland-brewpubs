package com.brewpubs.app.controllers;

import com.brewpubs.app.models.Credential;
import com.brewpubs.app.models.File;
import com.brewpubs.app.models.Note;
import com.brewpubs.app.models.User;
import com.brewpubs.app.services.BreweryService;
import com.brewpubs.app.services.CredentialService;
import com.brewpubs.app.services.FileService;
import com.brewpubs.app.services.NoteService;
import com.brewpubs.app.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// ––––– Created by Rajiv Shankar on 11/13/25 @ 8:06 PM ––––– //

/**
 * Controller: receives & processes incoming HTTP requests | returns appropriate views (HTML pages) to users |
 * key component in Spring's Model-View-Controller (MVC) architectural pattern |
 * Home-Controller manages requests to home-page
 */
@Controller
public class HomeController {

    // Field declaration: "shelf" to store (reserve a spot) the dependency for use throughout this class
    private final BreweryService breweryService;
    private final UserService userService;
    private final NoteService noteService;
    private final FileService fileService;
    private final CredentialService credentialService;


    // Constructor injection - Spring provides both services
    public HomeController(BreweryService breweryService,
                          UserService userService,
                          NoteService noteService,
                          FileService fileService,
                          CredentialService credentialService) {
        this.breweryService = breweryService;
        this.userService = userService;
        this.noteService = noteService;
        this.fileService = fileService;
        this.credentialService = credentialService;
        System.out.println("✅ HomeController initialized with BreweryService, UserService, NoteService, FileService");
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

        User user = null;

        // User-specific attributes
        if (principal != null) {
            String username = principal.getName();
            user = userService.getUser(username);  // ← Assign (no 'User' keyword!)
            System.out.println("🔐 Authenticated user: " + username);

            // Set authentication attributes
            if (user != null && user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                model.addAttribute("userFirstName", user.getFirstName());
            } else {
                model.addAttribute("userFirstName", username);
            }
            model.addAttribute("isAuthenticated", true);
        } else {
            // Anonymous user
            System.out.println("👤 Anonymous user visiting home");
            model.addAttribute("isAuthenticated", false);
        }

        // Load user's notes and files
        if (user != null) {  // ← Check user, not principal

            // Load notes
            List<Note> notes = noteService.getNotesByUserId(user.getUserId());
            model.addAttribute("notes", notes);

            // Load files
            List<File> files = fileService.getFilesByUserId(user.getUserId());
            model.addAttribute("files", files);

            System.out.println("📋 Loaded " + notes.size() + " notes for user " + user.getUsername());
            System.out.println("📁 Loaded " + files.size() + " files for user " + user.getUsername());

        } else {
            // Anonymous user - empty lists
            model.addAttribute("notes", new ArrayList<>());
            model.addAttribute("files", new ArrayList<>());
        }

        // Load credentials for this user
        List<Credential> credentials = credentialService.getCredentialsByUserId(user.getUserId());
        model.addAttribute("credentials", credentials);
        model.addAttribute("credential", new Credential()); // Empty for form

        // Add empty Note object for form binding
        model.addAttribute("note", new Note());

        return "home";
    }

    // ========== NOTE CRUD ENDPOINTS ==========

    /**
     * POST /notes - CREATE new note
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
    public String createNote(
            @ModelAttribute Note note,
            Principal principal,
            RedirectAttributes redirectAttributes) {
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
     * POST /notes/update - UPDATE existing note
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
     * GET /notes/delete/{id} - DELETE note by ID
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

    @PostMapping("/files/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        User user = userService.getUser(principal.getName());
        if (user == null) {
            redirectAttributes.addFlashAttribute("fileError",
                    "User not found. Please login again.");
            return "redirect:/";
        }

        String result = fileService.uploadFile(file, user.getUserId());

        switch (result) {
            case "success":
                redirectAttributes.addFlashAttribute("fileSuccess",
                        "File uploaded successfully!");
                break;
            case "error_duplicate":
                redirectAttributes.addFlashAttribute("fileError",
                        "You already have a file with this name.");
                break;
            default:
                redirectAttributes.addFlashAttribute("fileError",
                        "Upload failed. Please try again.");
        }
        return "redirect:/";
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer fileId,
                                               Principal principal) {

        User user = userService.getUser(principal.getName());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        File file = fileService.getFileById(fileId);

        // Security: verify ownership
        if (file == null || file.getUserId() != user.getUserId()) {
            return ResponseEntity.status(403).build();
        }

        // Build response with headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDispositionFormData("attachment", file.getFilename());

        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }

    @GetMapping("/files/delete/{fileId}")
    public String deleteFile(@PathVariable Integer fileId,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        User user = userService.getUser(principal.getName());
        boolean deleted = fileService.deleteFile(fileId, user.getUserId());

        if (deleted) {
            redirectAttributes.addFlashAttribute("fileSuccess",
                    "File deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("fileError",
                    "Could not delete file.");
        }
        return "redirect:/";
    }

    @PostMapping("/credentials/create")
    public String createCredential(@RequestParam("url") String url,
                                   @RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        User user = userService.getUser(principal.getName());
        if (user == null) {
            redirectAttributes.addFlashAttribute("credentialError",
                    "User not found. Please login again.");
            return "redirect:/";
        }

        String result = credentialService.createCredential(url, username, password,
                user.getUserId());

        if (result.equals("success")) {
            redirectAttributes.addFlashAttribute("credentialSuccess",
                    "Credential added successfully!");
        } else {
            redirectAttributes.addFlashAttribute("credentialError",
                    "Failed to add credential.");
        }

        return "redirect:/";
    }

    @PostMapping("/credentials/update")
    public String updateCredential(@RequestParam("credentialId") Integer credentialId,
                                   @RequestParam("url") String url,
                                   @RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        User user = userService.getUser(principal.getName());
        String result = credentialService.updateCredential(credentialId, url, username,
                password, user.getUserId());

        if (result.equals("success")) {
            redirectAttributes.addFlashAttribute("credentialSuccess",
                    "Credential updated successfully!");
        } else if (result.equals("unauthorized")) {
            redirectAttributes.addFlashAttribute("credentialError",
                    "Unauthorized access.");
        } else {
            redirectAttributes.addFlashAttribute("credentialError",
                    "Failed to update credential.");
        }

        return "redirect:/";
    }

    @GetMapping("/credentials/delete/{credentialId}")
    public String deleteCredential(@PathVariable Integer credentialId,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {

        User user = userService.getUser(principal.getName());
        boolean deleted = credentialService.deleteCredential(credentialId, user.getUserId());

        if (deleted) {
            redirectAttributes.addFlashAttribute("credentialSuccess",
                    "Credential deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("credentialError",
                    "Could not delete credential.");
        }

        return "redirect:/";
    }

    @GetMapping("/credentials/edit/{credentialId}")
    @ResponseBody
    public Credential getCredentialForEdit(@PathVariable Integer credentialId,
                                           Principal principal) {
        User user = userService.getUser(principal.getName());
        return credentialService.getCredentialWithDecryption(credentialId);
    }

}
