package com.brewpubs.app.services;

/**
 * Created by Rajiv Shankar on 1/9/26 @ 4:10 PM.
 */

import com.brewpubs.app.mappers.UserMapper;
import com.brewpubs.app.models.User;
import org.springframework.stereotype.Service;

/**
 * UserService - Business logic for user operations
 *
 * RESPONSIBILITIES:
 * - Check if username already exists (prevent duplicates)
 * - Create new users with hashed passwords
 * - Retrieve users for authentication
 *
 * FLOW FOR REGISTRATION:
 * 1. Controller receives form data (User object with plain text password)
 * 2. UserService.createUser() is called
 * 3. Generate unique salt for this user
 * 4. Hash password with salt
 * 5. Store user with salt and hashed password in database
 */
@Service
public class UserService {

    private final UserMapper userMapper;
    private final HashService hashService;

    // Constructor injection - Spring provides both dependencies
    public UserService(UserMapper userMapper, HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
        System.out.println("✅ UserService initialized with UserMapper and HashService");
    }

    /**
     * Check if username already exists in database
     * @param username The username to check
     * @return true if username is taken, false if available
     */
    public boolean isUsernameAvailable(String username) {
        return userMapper.getUserByUsername(username) == null;
    }

    /**
     * Create a new user with hashed password
     *
     * @param user User object with plain text password from registration form
     * @return Number of rows inserted (1 if successful)
     *
     * IMPORTANT: This method modifies the User object:
     * - Sets the salt field
     * - Replaces plain text password with hashed password
     */
    public int createUser(User user) {
        // Step 1: Generate unique salt for this user
        String salt = hashService.generateSalt();
        user.setSalt(salt);

        // Step 2: Hash the password with the salt
        String hashedPassword = hashService.getHashedPassword(user.getPassword(), salt);
        user.setPassword(hashedPassword);  // Replace plain text with hash

        // Step 3: Insert into database
        System.out.println("✅ Creating user: " + user.getUsername());
        return userMapper.insert(user);
    }

    /**
     * Get user by username (for authentication)
     * @param username The username to look up
     * @return User if found, null otherwise
     */
    public User getUser(String username) {
        return userMapper.getUserByUsername(username);
    }
}

