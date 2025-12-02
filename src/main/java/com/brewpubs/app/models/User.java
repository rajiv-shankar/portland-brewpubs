package com.brewpubs.app.models;

/**
 * Created by Rajiv Shankar on 11/19/25 @ 11:22 AM.
 */

import jakarta.validation.constraints.*;

/**
 * `User` model class (domain class = entity = POJO) representing a registered `user`
 *
 * PURPOSE: Hold user data that will be bound from HTML form
 *
 * BINDING PROCESS:
 * 1. User fills form in register.html
 * 2. Form submits to POST /register
 * 3. Spring creates User object
 * 4. Spring sets fields from form (via @ModelAttribute)
 * 5. Controller receives populated User object
 */
public class User {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    /**
     * Default constructor (required by Spring for form binding)
     * Spring will call this to create empty User object
     * Then set username and email from form data
     */
    public User() {
    }

    /**
     * Constructor with all fields
     * Used when creating User objects programmatically
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

// Getters and Setters (required for @ModelAttribute binding)

    /**
     * Get username
     * FORM BINDING: When form has <input th:field="*{username}"/>,
     * Spring calls setUsername() to set this value
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get email
     * FORM BINDING: When form has <input th:field="*{email}"/>,
     * Spring calls setEmail() to set this value
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

