package com.brewpubs.app;

/**
 * Created by Rajiv Shankar on 11/13/25 @ 8:06 PM.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller: key component in Spring's Model-View-Controller (MVC) architectural pattern
 * receives & processes incoming HTTP requests
 * HomeController manages requests to homepage
 */

@Controller  // receive & process web requests and return views
public class HomeController {

    @Autowired
    private BreweryService breweryService;

    @GetMapping("/")  // manage GET requests from "/" (homepage)
    public String home(Model model) {  // Model: holds data to be rendered in the view

        // add data to the model
        model.addAttribute("cityName", "Portland"); // key:value pair
        model.addAttribute("stateName", "Maine");

//        model.addAttribute("breweryCount", 20);
        // dynamic: get brewery count from service layer
        model.addAttribute("breweryCount", breweryService.getBreweryCount());

        return "home";  // render home.html template with model data
    }
}