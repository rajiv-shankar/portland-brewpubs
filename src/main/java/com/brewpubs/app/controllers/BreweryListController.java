package com.brewpubs.app.controllers;

/**
 * Created by Rajiv Shankar on 11/14/25 @ 12:55 PM.
 */

import com.brewpubs.app.services.BreweryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * manages requests to /breweries page
 */
@Controller
public class BreweryListController {
    private final BreweryService breweryService;  // injected service layer to get brewery data

    public BreweryListController(BreweryService breweryService) {   // dependency injection via constructor
        this.breweryService = breweryService;
    }

    @GetMapping("/breweries")
    public String listBreweries(Model model) {  // Model (Spring class): holds data — objects that represent app's information

/*
        // ❌ PROBLEM: Creating data IN the controller i/o using a service layer (BreweryService.java)
        // Create a list to hold brewery objects
        List<Brewery> breweries = new ArrayList<>(); // ArrayList: resizable array implementation of List interface

        // Add breweries to the list (in a real app, this data would come from a database)
        breweries.add(new Brewery(
                "Allagash Brewing Company",
                "50 Industrial Way, Portland",
                "Allagash White"
        ));

        breweries.add(new Brewery(
                "Bissell Brothers Brewing",
                "4 Thompson's Point Road, Portland",
                "The Substance (Double IPA)"
        ));

        breweries.add(new Brewery(
                "Foundation Brewing Company",
                "1 Industrial Way, Portland",
                "Epiphany (IPA)"
        ));

        breweries.add(new Brewery(
                "Austin Street Brewery",
                "1 Industrial Way, Portland",
                "Patina (Pale Ale)"
        ));

        breweries.add(new Brewery(
                "Rising Tide Brewing Company",
                "103 Fox Street, Portland",
                "Daymark (Pale Ale)"
        ));

        // Add the list to the model; the template can now access this list
        model.addAttribute("breweryList", breweries); // key:breweryList, value:breweries list
        model.addAttribute("totalCount", breweries.size()); // breweries.size(): calcs # of breweries in the list

        // ✅ SOLUTION: Call service, don't create data here →
*/
        // for navigation bar
        model.addAttribute("currentPage", "breweries");
        // Use BreweryService to get brewery data
        model.addAttribute("breweryList", breweryService.getAllBreweries());
        model.addAttribute("totalCount", breweryService.getBreweryCount());

        return "brewery-list";  // Show brewery-list.html
    }
}

