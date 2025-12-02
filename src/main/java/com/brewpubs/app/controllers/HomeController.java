package com.brewpubs.app.controllers;

/**
 * Created by Rajiv Shankar on 11/13/25 @ 8:06 PM.
 */

import com.brewpubs.app.services.BreweryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller: receives & processes incoming HTTP requests | returns appropriate views (HTML pages) to users |
 * key component in Spring's Model-View-Controller (MVC) architectural pattern |
 * HomeController manages requests to homepage
 */

@Controller  // Spring: mark class as a web controller for handling user requests (receive & process web requests and return views)
public class HomeController {

    // Field declaration: "shelf" to store (reserves a spot) the dependency for use throughout this class
    private final BreweryService breweryService;

    // Constructor: Spring calls this automatically at startup
    // No @Autowired needed – Spring auto-detects this single constructor
    // Spring already created a BreweryService instance (due to @Service annotation on BreweryService)
    // and passes it here as the argument — no "new BreweryService()" needed
    public HomeController(BreweryService breweryService) {  // constructor
        this.breweryService = breweryService;  // Spring: inject dependency
    }

    @GetMapping("/")  // manage GET requests from "/" (homepage)
    public String home(Model model) {  // Model: Spring's Model object, holds data to be rendered in the view; automatically created and supplied by Spring MVC ("invisble")

        // add data to the model
        model.addAttribute("cityName", "Portland"); // key:value pair
        model.addAttribute("stateName", "Maine");

        // model.addAttribute("breweryCount", 20);
        // dynamic: get brewery count from service layer
        model.addAttribute("breweryCount", breweryService.getBreweryCount());

        return "home";  // render home.html template with model data
    }
}