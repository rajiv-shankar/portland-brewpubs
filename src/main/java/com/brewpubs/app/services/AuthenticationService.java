package com.brewpubs.app.services;

/**
 * Created by Rajiv Shankar on 1/9/26 @ 4:26 PM.
 */

import com.brewpubs.app.mappers.UserMapper;
import com.brewpubs.app.models.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * AuthenticationService - Custom AuthenticationProvider for Spring Security
 *
 * This class replaces the InMemoryUserDetailsManager from Day 7.
 * Instead of checking against hardcoded users, it:
 * 1. Looks up the user in the database
 * 2. Retrieves their salt
 * 3. Hashes the entered password with that salt
 * 4. Compares with stored hash
 *
 * IMPLEMENTS: AuthenticationProvider interface
 * Spring Security calls authenticate() when user submits login form
 */
@Service
public class AuthenticationService implements AuthenticationProvider {

    private final UserMapper userMapper;
    private final HashService hashService;

    public AuthenticationService(UserMapper userMapper, HashService hashService) {
        this.userMapper = userMapper;
        this.hashService = hashService;
        System.out.println("✅ AuthenticationService initialized");
    }

    /**
     * Called by Spring Security when user attempts to log in
     *
     * @param authentication Contains the username and password from login form
     * @return Authentication token if successful
     * @throws AuthenticationException if credentials are invalid
     */
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        // Extract username and password from the login attempt
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println("🔐 Authentication attempt for: " + username);

        // Step 1: Look up user in database
        User user = userMapper.getUserByUsername(username);

        if (user == null) {
            System.out.println("❌ User not found: " + username);
            throw new BadCredentialsException("Invalid username or password");
        }

        // Step 2: Hash the entered password with the user's stored salt
        String hashedPassword = hashService.getHashedPassword(password, user.getSalt());

        // Step 3: Compare with stored hash
        if (!hashedPassword.equals(user.getPassword())) {
            System.out.println("❌ Invalid password for: " + username);
            throw new BadCredentialsException("Invalid username or password");
        }

        // Step 4: Success! Return authenticated token
        System.out.println("✅ Authentication successful for: " + username);
        return new UsernamePasswordAuthenticationToken(
                username,
                password,
                new ArrayList<>()  // Empty authorities list (no roles for now)
        );
    }

    /**
     * Tells Spring Security which authentication types this provider supports
     * We support username/password authentication
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

