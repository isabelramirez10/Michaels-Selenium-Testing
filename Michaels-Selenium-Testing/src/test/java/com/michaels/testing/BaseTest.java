package com.michaels.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait; // Added for reusable explicit waits
    protected static final String BASE_URL = "https://www.michaels.com";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");

        // Use a realistic user agent
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Standardized wait

        // Remove "webdriver" flag via Javascript
        ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        driver.get(BASE_URL);

        // Handle the initial newsletter popup that breaks tests
        handleInitialPopups();
    }

    /**
     * Requirement 3 Strategy: Address automation constraints (Pop-ups)
     */
    private void handleInitialPopups() {
        try {
            // Wait up to 5 seconds for the "X" button of the newsletter popup to appear
            By closeButtonXpath = By.xpath("//button[@aria-label='Close' or contains(@class, 'close')]");
            WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeButtonXpath));
            closeButton.click();
            System.out.println("Promo popup closed.");
        } catch (Exception e) {
            System.out.println("No promo popup appeared within timeout.");
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}