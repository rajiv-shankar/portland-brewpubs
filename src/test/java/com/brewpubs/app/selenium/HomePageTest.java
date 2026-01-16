package com.brewpubs.app.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Rajiv Shankar on 1/15/26 @ 6:17 PM.
 */

/**
 * HomePageTest - Selenium test for homepage functionality
 *
 * SETUP:
 * - @SpringBootTest(webEnvironment = RANDOM_PORT) starts real server on random port
 * - @LocalServerPort injects the actual port number
 * - WebDriverManager.chromedriver().setup() downloads ChromeDriver
 * - ChromeDriver launches real Chrome browser
 *
 * LIFECYCLE:
 * - @BeforeAll: Runs once before all tests (setup driver)
 * - @BeforeEach: Runs before each test (navigate to homepage)
 * - @Test: The actual test method
 * - @AfterEach: Runs after each test (cleanup if needed)
 * - @AfterAll: Runs once after all tests (close browser)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)  // Allow non-static @BeforeAll
class HomePageTest {

    @LocalServerPort
    private int port;  // Spring injects random port

    private WebDriver driver;  // Browser controller

    @Value("${selenium.browser:brave}")  // Default to Brave if not specified
    private String browserName;

    /**
     * Setup WebDriverManager - runs ONCE before all tests
     */
    @BeforeAll
    public void setupDriverManager() {
        System.out.println("🌐 Configuring WebDriver for browser: " + browserName);

        // Setup appropriate driver based on browser choice
        switch (browserName.toLowerCase()) {
            case "brave":
            case "chrome":
            case "chromium":
                WebDriverManager.chromedriver().setup();
                System.out.println("✅ ChromeDriver configured (works for Chrome, Chromium, Brave)");
                break;

            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                System.out.println("✅ GeckoDriver configured (Firefox)");
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                System.out.println("✅ EdgeDriver configured (Microsoft Edge)");
                break;

            default:
                System.out.println("⚠️ Unknown browser '" + browserName + "', defaulting to Chrome");
                WebDriverManager.chromedriver().setup();
        }
    }

    /**
     * Create browser instance - runs BEFORE EACH test
     */
    @BeforeEach
    public void setupBrowser() {
        // Create new Chrome browser instance
        driver = createWebDriver(browserName);
        System.out.println("✅ Browser launched: " + browserName);
    }

    /**
     * Cleanup method - runs AFTER EACH test
     */
    @AfterEach
    public void tearDown() {
        // Close browser after test
        if (driver != null) {
            driver.quit();
            System.out.println("✅ Browser closed");
        }
    }

    /**
     * Factory method: Create appropriate WebDriver based on browser name
     *
     * BRAVE CONFIGURATION:
     * - Brave is Chromium-based → use ChromeDriver
     * - ChromeOptions.setBinary() points to Brave executable
     * - WebDriverManager auto-detects Brave location
     */
    private WebDriver createWebDriver(String browser) {
        switch (browser.toLowerCase()) {
            case "brave":
                ChromeOptions braveOptions = new ChromeOptions();
                // WebDriverManager auto-detects Brave binary location
                // Common paths: /Applications/Brave Browser.app (Mac)
                //              C:\Program Files\BraveSoftware\Brave-Browser (Windows)
                //              /usr/bin/brave-browser (Linux)
                return new ChromeDriver(braveOptions);

            case "chrome":
            case "chromium":
                ChromeOptions chromeOptions = new ChromeOptions();
                return new ChromeDriver(chromeOptions);

            case "firefox":
                return new FirefoxDriver();

            case "edge":
                return new EdgeDriver();

            default:
                System.out.println("⚠️ Unknown browser, using Chrome");
                return new ChromeDriver();
        }
    }

    /**
     * Test 1: Homepage loads successfully
     */
    @Test
    public void testHomePageLoads() {
        // Navigate to homepage
        String url = "http://localhost:" + port + "/";
        driver.get(url);
        System.out.println("📍 Navigated to: " + url);

        // Get page title
        String title = driver.getTitle();
        System.out.println("📄 Page title: " + title);

        // Verify title
        assertEquals("Portland Brewpubs", title, "Page title should be 'Portland Brewpubs'");

        System.out.println("✅ Test passed: Homepage loaded successfully");
    }

    /**
     * Test 2: Homepage displays brewery count
     */
    @Test
    public void testBreweryCountDisplayed() {
        // Navigate to homepage
        driver.get("http://localhost:" + port + "/");

        // Find element containing brewery count
        // This looks for element with class="number"
        WebElement breweryCountElement = driver.findElement(By.className("number"));
        String breweryCount = breweryCountElement.getText();

        System.out.println("🍺 Brewery count displayed: " + breweryCount);

        // Verify count is "6" (from database)
        assertEquals("6", breweryCount, "Should display 6 breweries");

        System.out.println("✅ Test passed: Brewery count is correct");
    }

    /**
     * Test 3: Anonymous user sees register link
     */
    @Test
    public void testAnonymousUserSeesRegisterLink() {
        // Navigate to homepage
        driver.get("http://localhost:" + port + "/");

        System.out.println("🔍 Testing anonymous user view...");

        // Find the main welcome div (the one that shows for anonymous users)
        // It should contain the h2 heading
        try {
            // Find h2 element (heading)
            WebElement heading = driver.findElement(By.tagName("h2"));
            String headingText = heading.getText().trim();

            System.out.println("📄 Heading text: " + headingText);

            // Anonymous users see "Welcome to Portland, Maine!"
            // Authenticated users see "Welcome back, [name]!"
            assertTrue(headingText.startsWith("Welcome to"),
                    "Heading should start with 'Welcome to'");
            assertTrue(headingText.contains("Portland"),
                    "Heading should mention Portland");

            // Verify register link exists and is clickable
            WebElement registerLink = driver.findElement(By.linkText("Register"));
            assertTrue(registerLink.isDisplayed(), "Register link should be visible");

            System.out.println("✅ Found 'Register' link");

            // Verify call-to-action text
            String bodyText = driver.findElement(By.tagName("body")).getText();
            assertTrue(bodyText.contains("to save your favorites"),
                    "Should show registration call-to-action");

            System.out.println("✅ Test passed: Anonymous user sees correct content");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());

            // Debug: Print entire page source
            System.err.println("=== PAGE SOURCE ===");
            System.err.println(driver.getPageSource());
            System.err.println("===================");

            fail("Could not verify anonymous user view: " + e.getMessage());
        }
    }
}