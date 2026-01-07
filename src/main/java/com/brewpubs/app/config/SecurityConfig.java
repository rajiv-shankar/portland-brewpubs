package com.brewpubs.app.config;

/**
 * Created by Rajiv Shankar on 1/7/26 @ 2:11 PM.
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Spring Security 6+ configuration
 *
 * KEY CONCEPTS:
 * 1. @Configuration - Marks class as a source of bean definitions
 * 2. @EnableWebSecurity - Enables Spring Security's web security support
 * 3. SecurityFilterChain - Defines which URLs require authentication
 * 4. UserDetailsService - Provides user data for authentication
 * 5. PasswordEncoder - Hashes passwords (never store plain text!)
 *
 * SPRING SECURITY 6 vs OLDER VERSIONS:
 * - OLD: extends WebSecurityConfigurerAdapter (DEPRECATED)
 * - NEW: @Bean methods returning SecurityFilterChain
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * SecurityFilterChain - The main security configuration
     *
     * This method configures:
     * - Which URLs are public (permitAll)
     * - Which URLs require authentication (authenticated)
     * - The login page location
     * - The logout behavior
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configure URL-based authorization
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC PAGES - No login required
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                        // H2 Console - Allow access for development
                        .requestMatchers("/h2-console/**").permitAll()
                        // PROTECTED PAGES - Login required
                        .requestMatchers("/breweries/**").authenticated()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // Configure login
                .formLogin(form -> form
                        .loginPage("/login")           // Custom login page URL
                        .loginProcessingUrl("/login")  // URL that processes login POST
                        .defaultSuccessUrl("/breweries", true) // Redirect after successful login
                        .failureUrl("/login?error=true") // Redirect on login failure
                        .permitAll()                    // Allow everyone to see login page
                )
                // Configure logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // H2 Console requires these settings
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        System.out.println("✅ SecurityConfig initialized");
        return http.build();
    }

    /**
     * UserDetailsService - Provides users for authentication
     *
     * FOR NOW: In-memory users (hardcoded for testing)
     * TOMORROW: Will replace with database-backed users (UserMapper)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Create a test user (in-memory, not in database yet)
        UserDetails testUser = User.builder()
                .username("rajiv")
                .password(passwordEncoder().encode("password123"))
                .roles("USER")
                .build();

        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("USER", "ADMIN")
                .build();

        System.out.println("✅ Created test users: rajiv, admin");
        return new InMemoryUserDetailsManager(testUser, adminUser);
    }

    /**
     * PasswordEncoder - Hashes passwords using BCrypt
     *
     * NEVER store passwords as plain text!
     * BCrypt automatically handles salting and is resistant to rainbow table attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

