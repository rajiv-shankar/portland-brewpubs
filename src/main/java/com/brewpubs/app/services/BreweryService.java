package com.brewpubs.app.services;

/**
 * Created by Rajiv Shankar on 11/14/25 @ 5:41 PM.
 */

import com.brewpubs.app.mappers.BreweryMapper;
import com.brewpubs.app.models.Brewery;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVICE LAYER
 * Business logic layer for brewery operations
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

/* VERSION-1: method-based implementation: repetitive: creates NEW ArrayList each time it is called

    public List<Brewery> getAllBreweries() { // returns a list of all breweries
        List<Brewery> breweries = new ArrayList<>();
        breweries.add(new Brewery("Allagash Brewing Company", "50 Industrial Way, Portland", "Allagash White"));
        breweries.add(new Brewery("Bissell Brothers Brewing", "4 Thompson’s Point Road, Portland", "The Substance (IPA)"));
        breweries.add(new Brewery("Foundation Brewing Company", "1 Industrial Way, Portland", "Epiphany (IPA)"));
        breweries.add(new Brewery("Maine Beer Company", "525 US Route 1, Freeport", "Lunch (IPA)"));
        return breweries;  // return list of breweries, to be used by other components (e.g., controllers)
    }
 */

/* VERSION-2: constructor-based (?) implementation:

    private List<Brewery> breweries;

    // constructor: initialize brewery data once when service is created > store in field (this.breweries) >
    // Easy to swap for database later without changing methods
    public BreweryService() {
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
 */

/**
     * VERSION-3: mapper-based (?) implementation:
     *
     * ARCHITECTURE:
     * Controller → Service → Mapper → Database
     *
     * BEFORE: Hardcoded list of breweries in constructor
     * AFTER: Delegates all data operations to BreweryMapper
     *
     * WHY KEEP THE SERVICE LAYER?
     * - Controllers shouldn't talk directly to mappers
     * - Service layer can add business logic (validation, transformations)
     * - Easier to test (mock the mapper)
     * - Can combine multiple mapper calls in one transaction
     */

        private final BreweryMapper breweryMapper;

        // Constructor injection - Spring injects the mapper
        public BreweryService(BreweryMapper breweryMapper) {
            this.breweryMapper = breweryMapper;
            System.out.println("✅ BreweryService initialized with database mapper");
        }

        // ========== READ OPERATIONS ==========

        public List<Brewery> getAllBreweries() {
            return breweryMapper.getAllBreweries();
        }

        public Brewery getBreweryById(Integer id) {
            return breweryMapper.getBreweryById(id);
        }

        public int getBreweryCount() {
            return breweryMapper.getBreweryCount();
        }

        // ========== CREATE OPERATIONS ==========

        public int addBrewery(Brewery brewery) {
            return breweryMapper.insert(brewery);
        }

        // ========== UPDATE OPERATIONS ==========

        public int updateBrewery(Brewery brewery) {
            return breweryMapper.update(brewery);
        }

        // ========== DELETE OPERATIONS ==========

        public int deleteBrewery(Integer id) {
            return breweryMapper.delete(id);
        }
    }



