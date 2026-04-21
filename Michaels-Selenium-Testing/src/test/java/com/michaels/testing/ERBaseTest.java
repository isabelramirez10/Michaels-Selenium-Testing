package com.michaels.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import java.time.Duration;

public class ERBaseTest {
    protected WebDriver driver;
    // Removed the trailing slash to prevent double-slash errors (//)
    protected final String BASE_URL = "https://www.michaels.com";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    protected void handleGlobalPopups() {
        try {
            WebDriverWait quickWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement acceptCookies = quickWait.until(ExpectedConditions.elementToBeClickable(
                    By.id("onetrust-accept-btn-handler")));
            acceptCookies.click();
            System.out.println(">> Cookie popup dismissed.");
        } catch (Exception e) {
            // Popup not present
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}