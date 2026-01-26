package com.brewpubs.app.controllers;

import com.brewpubs.app.models.User;
import com.brewpubs.app.services.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

// ––––– Created by Rajiv Shankar on 11/19/25 @ 2:20 PM ––––– //

/**
 * Controller for user registration.
 *
 * RESPONSIBILITY:
 * - Handle GET /register → Show registration form
 * - Handle POST /register → Process form submission
 * - Pass data to views
 *
 * LAYERED ARCHITECTURE:
 * View: register.html, confirmation.html (Thymeleaf templates)
 * Controller: RegistrationController (HTTP request handling)
 * Service: UserService (would go here - see tomorrow)
 * Model: User.java (data structure)
 */
@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * HANDLE GET REQUEST: Show blank registration form
     *
     * WHAT HAPPENS:
     * 1. User visits http://localhost:8080/register
     * 2. Spring routes to this method
     * 3. We create empty User object [new User()] (value)
     * 4. We add it to Model with key "user"
     * 5. Spring renders register.html with empty form
     * 6. User sees registration form with blank fields
     *
     * @ param model Spring's Model object (auto-injected by Spring)
     * @ return template name "register" (renders register.html)
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // for navigation bar
        model.addAttribute("currentPage", "register");
        model.addAttribute("user", new User());
        return "register";
    }

    /**
     * HANDLE POST REQUEST: Process form submission
     *
     * @ModelAttribute BINDING EXPLAINED:
     * When form submits, Spring:
     * 1. Creates new User object
     * 2. Reads form data (username, email)
     * 3. Calls user.setUsername(formValue)
     * 4. Calls user.setEmail(formValue)
     * 5. Passes populated User object to this method
     *
     * WHAT HAPPENS:
     * 1. User fills form and clicks "Register"
     * 2. Form submits POST to /register with form data
     * 3. Spring creates User object with form data
     * 4. We receive User object with username and email filled in
     * 5. We add to model for confirmation page
     * 6. Confirmation page displays "Welcome, [username]!"
     * 7. User sees success confirmation
     *
     * WHAT HAPPENS (NEXT):
     * POST /register - Process form with VALIDATION
     * 1. Add @Valid before User parameter → Enforces validation
     * 2. Add BindingResult after User → Captures validation errors
     * 3. Check hasErrors() → If true, validation failed
     * 4. If errors exist, return "register" to redisplay form with errors
     *
     * @param user The User object with form data (autopopulated by @ModelAttribute)
     * @param model Spring's Model object
     * @return template name "confirmation" (renders confirmation.html)
     */

    @PostMapping("/register")
    public String processRegistration(
        // @Valid enforces validation:
        // 1. Creates `User` object from form data,
        // 2. Runs validation (@NotBlank, @Email),
        // 3. Creates a `BindingResult` to hold any validation errors,
        // 4. Passes both to the method
            @Valid User user,
            BindingResult bindingResult,
            Model model) {
        // for navigation bar
        model.addAttribute("currentPage", "register");

        // Step 1: Check for validation errors
        if (bindingResult.hasErrors()) {
            // Print errors to console for debugging
            System.out.println("❌ Validation failed!");
            // capture validation errors; loop through all errors and print messages
            bindingResult.getAllErrors().forEach(error ->
                    System.out.println("  - " + error.getDefaultMessage())
            );
        //  Return "register" page to redisplay form with errors: will show user's typed values + error messages
            return "register";
        }

        // Step 2: Check if username already exists
        if (!userService.isUsernameAvailable(user.getUsername())) {
            System.out.println("❌ Username already exists: " + user.getUsername());
            model.addAttribute("signupError", "Username already exists. Please choose another.");
            return "register";
        }

        // Step 3: Create user in database
        try {
            int rowsInserted = userService.createUser(user);
            if (rowsInserted > 0) {
                System.out.println("✅ User created successfully: " + user.getUsername());
                System.out.println("  Email: " + user.getEmail());
                model.addAttribute("signupSuccess", true);
                // Redirect to login page with success message
                return "redirect:/login?registered=true";
            }
        } catch (Exception e) {
            System.out.println("❌ Error creating user: " + e.getMessage());
            model.addAttribute("signupError", "Registration failed. Please try again.");
        }

        return "register";
    }
}