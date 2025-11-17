package com.brewpubs.app;

/**
 * Created by Rajiv Shankar on 11/14/25 @ 5:41 PM.
 */

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVICE LAYER
 *
 * SERVICE RESPONSIBILITY: All brewery business logic and data management
 * - Creating brewery objects
 * - Returning brewery lists
 * - Counting breweries
 * - (Later: database queries, API calls, etc.)
 *
 * CONTROLLER-SERVICE PATTERN:
 * Controllers handle HTTP requests → Services handle business logic
 * This separation makes code:
 * - Testable (test service without HTTP)
 * - Reusable (multiple controllers can use same service)
 * - Maintainable (change business logic in ONE place)
 * - Replaceable (swap service for database later)
 * - Cleaner (controllers focus on request/response)
 * - Scalable (add more logic without bloating controllers)
 * - Organized (clear separation of concerns)
 * - Decoupled (less dependency between layers)
 * - Efficient (optimize service logic independently)
 * - Consistent (centralized business rules)
 * - Flexible (evolve service without affecting controllers)
 * Overall, using a service layer like BreweryService improves code quality, architecture, and long-term maintainability of the application.
 */

@Service  // register this class as a Service bean
public class BreweryService {

    private List<Brewery> breweries;

    public BreweryService() {  // constructor: initialize brewery data once when service is created > store in field (this.breweries) > Easy to swap for database later without changing methods
        this.breweries = new ArrayList<>();

        this.breweries.add(new Brewery(
                "Allagash Brewing Company",
                "50 Industrial Way, Portland, ME",
                "Allagash White"
        ));

        this.breweries.add(new Brewery(
                "Bissell Brothers Brewing",
                "38 Resurgam Pl, Portland, ME",
                "The Substance (Double IPA)"
        ));

        this.breweries.add(new Brewery(
                "Foundation Brewing Company",
                "1 Industrial Way, Portland, ME",
                "Epiphany (IPA)"
        ));

        this.breweries.add(new Brewery(
                "Austin Street Brewery",
                "391 Congress St, Portland, ME",
                "Neverender (Double IPA)"
        ));

        this.breweries.add(new Brewery(
                "Rising Tide Brewing Company",
                "103 Fox St, Portland, ME",
                "Daymark (Pale Ale)"
        ));

        this.breweries.add(new Brewery(
                "Mast Landing Brewing Company",
                "200 Lower Main St, Freeport, ME",
                "Pantless Thunder Goose (Imperial IPA)"
        ));

        System.out.println("✅ BreweryService initialized with " + breweries.size() + " breweries");
    }

    public List<Brewery> getAllBreweries() { // O(1) - instant list access:
        return this.breweries;
    }

    public int getBreweryCount() {
        return this.breweries.size();
    }

    public Brewery getBreweryByName(String name) {
        return this.breweries.stream()
                .filter(b -> b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }


/* method-based implementation: repetitive: creates NEW ArrayList each time it is called

public class BreweryService {

    public List<Brewery> getAllBreweries() { // returns a list of all breweries
        List<Brewery> breweries = new ArrayList<>();
        breweries.add(new Brewery("Allagash Brewing Company", "50 Industrial Way, Portland", "Allagash White"));
        breweries.add(new Brewery("Bissell Brothers Brewing", "4 Thompson’s Point Road, Portland", "The Substance (IPA)"));
        breweries.add(new Brewery("Foundation Brewing Company", "1 Industrial Way, Portland", "Epiphany (IPA)"));
        breweries.add(new Brewery("Maine Beer Company", "525 US Route 1, Freeport", "Lunch (IPA)"));
        return breweries;  // return list of breweries, to be used by other components (e.g., controllers)
    }*/
}


