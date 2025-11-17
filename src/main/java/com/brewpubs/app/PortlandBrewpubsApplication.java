package com.brewpubs.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Portland Brewpubs application.
 *
 * A @SpringBootApplication annotation does 3 things:
 * 1. @Configuration: source of bean definitions for the application context
 * 2. @EnableAutoConfiguration: enables Spring Boot's auto-configuration mechanism: attempts to automatically configure application based on dependencies
 * 3. @ComponentScan: Enables component scanning (finds @Controller, @Service classes)
 */
@SpringBootApplication // @Configuration + @EnableAutoConfiguration + @ComponentScan
public class PortlandBrewpubsApplication {
    /**
     * public: method can be accessed from anywhere;
     * static: method belongs to the class itself, not to any specific object of the class (no need to create an instance to call it);
     * void: method does not return any value after it is executed;
     * String[]: parameter is an array of String objects;
     * args: name of String array (can be named anything else)
     */
    public static void main(String[] args) {
                SpringApplication.run(PortlandBrewpubsApplication.class, args);
    }

}
