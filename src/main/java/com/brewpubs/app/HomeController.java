package com.brewpubs.app;

/**
 * Created by Rajiv Shankar on 11/13/25 @ 8:06 PM.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller: key component in Spring's Model-View-Controller (MVC) architectural pattern |
 * receives & processes incoming HTTP requests | returns appropriate views (HTML pages) to users |
 * HomeController manages requests to homepage
 */

@Controller  // Spring: mark class as a web controller for handling user requests (receive & process web requests and return views)
public class HomeController {

/*    @Autowired  // automatic injection of BreweryService dependency; else have to instantiate it manually
                // field injection: Spring injects BreweryService into this private field using reflection zz??
    private BreweryService breweryService;*/

    private final BreweryService breweryService;  // breweryService: dependency

    // No @Autowired needed! Spring auto-detects this single constructor
    public HomeController(BreweryService breweryService) {  // constructor
        this.breweryService = breweryService;  // Spring: inject dependency
    }

    @GetMapping("/")  // manage GET requests from "/" (homepage)
    public String home(Model model) {  // Model: holds data to be rendered in the view; automatically created and supplied by Spring MVC ("invisble")

        // add data to the model
        model.addAttribute("cityName", "Portland"); // key:value pair
        model.addAttribute("stateName", "Maine");

//        model.addAttribute("breweryCount", 20);
        // dynamic: get brewery count from service layer
        model.addAttribute("breweryCount", breweryService.getBreweryCount());

        return "home";  // render home.html template with model data
    }
}