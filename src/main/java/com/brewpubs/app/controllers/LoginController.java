package com.brewpubs.app.controllers;

/**
 * Created by Rajiv Shankar on 1/7/26 @ 3:18 PM.
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * LoginController - Handles login page display
 *
 * NOTE: We don't handle the POST /login here!
 * Spring Security intercepts POST /login automatically
 * and handles authentication for us.
 *
 * Our job is just to:
 * 1. Show the login form (GET /login)
 * 2. Display error messages if login failed
 * 3. Display success message after logout
 */
@Controller
public class LoginController {

    /**
     * Display login page
     *
     * @param error - Present if login failed (from SecurityConfig failureUrl)
     * @param logout - Present if user just logged out (from SecurityConfig logoutSuccessUrl)
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        // Add messages for error/logout states
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password.");
            System.out.println("❌ Login failed - invalid credentials");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
            System.out.println("✅ User logged out successfully");
        }

        // for navigation bar
        model.addAttribute("currentPage", "login");

        return "login";
    }
}

