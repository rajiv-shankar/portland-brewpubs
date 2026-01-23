package com.brewpubs.app.controllers;

/**
 * Created by Rajiv Shankar on 11/13/25 @ 8:06 PM.
 */

import com.brewpubs.app.models.User;
import com.brewpubs.app.services.BreweryService;
import com.brewpubs.app.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * Controller: receives & processes incoming HTTP requests | returns appropriate views (HTML pages) to users |
 * key component in Spring's Model-View-Controller (MVC) architectural pattern |
 * HomeController manages requests to homepage
 */
@Controller
public class HomeController {

    // Field declaration: "shelf" to store (reserves a spot) the dependency for use throughout this class
    private final BreweryService breweryService; // injected dependency
    private final UserService userService;

    // Constructor injection - Spring provides both services
    public HomeController(BreweryService breweryService, UserService userService) {
        this.breweryService = breweryService;
        this.userService = userService;
        System.out.println("✅ HomeController initialized with BreweryService and UserService");
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

        return "home";
    }
}
