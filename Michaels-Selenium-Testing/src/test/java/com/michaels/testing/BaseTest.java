package com.michaels.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    protected WebDriver driver;
    protected static final String BASE_URL = "https://www.michaels.com";

    // Test Credentials
    protected static final String TEST_EMAIL = "erocha3976@eagle.fgcu.edu";
    protected static final String TEST_PASSWORD = "SoftwareTesting123";
    protected static final String TEST_FIRST_NAME = "Michael";
    protected static final String TEST_LAST_NAME = "Testing";

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // Helps avoid some bot detection
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}