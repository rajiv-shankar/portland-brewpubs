package com.brewpubs.app;

/**
 * Created by Rajiv Shankar on 11/19/25 @ 2:20 PM.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

// Future: Will inject UserService here
// @Autowired
// private UserService userService;

    /**
     * HANDLE GET REQUEST: Show blank registration form
     *
     * WHAT HAPPENS:
     * 1. User visits http://localhost:8080/register
     * 2. Spring routes to this method
     * 3. We create empty User object
     * 4. We add it to Model with key "user"
     * 5. Spring renders register.html with empty form
     * 6. User sees registration form with blank fields
     *
     * @param model Spring's Model object (auto-injected by Spring)
     * @return template name "register" (renders register.html)
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        User user = new User();  // Create empty User object for form binding

        model.addAttribute("user", user);  // Add empty User to model with key "user"

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
     * @param user The User object with form data (auto-populated by @ModelAttribute)
     * @param model Spring's Model object
     * @return template name "confirmation" (renders confirmation.html)
     */
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, Model model) {
        // At this point, 'user' object has username and email from form!
        System.out.println("✅ Registration submitted: " + user);

        // Add user to model so confirmation.html can display it
        model.addAttribute("user", user);

    // TODO (Week 2): Save to database via UserService
    // userService.saveUser(user);

    // Return template name (Thymeleaf will find confirmation.html)
        return "confirmation";
    }
}

