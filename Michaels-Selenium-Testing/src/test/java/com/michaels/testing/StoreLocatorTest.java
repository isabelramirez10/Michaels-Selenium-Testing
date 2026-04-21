package com.michaels.testing;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Collections;

public class StoreLocatorTest {
    WebDriver driver;
    WebDriverWait wait;
    private final String LOCATOR_URL = "https://www.michaels.com/store-locator";

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(LOCATOR_URL);
    }

    @Test(priority = 1)
    public void testSelectState() {
        WebElement floridaLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Florida")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", floridaLink);
        floridaLink.click();
        Assert.assertTrue(driver.getCurrentUrl().contains("fl"), "URL should update to Florida");
        System.out.println("Test 1: Florida Selected");
    }

    @Test(priority = 2, dependsOnMethods = "testSelectState")
    public void testSelectCity() {
        WebElement esteroLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Estero")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", esteroLink);
        esteroLink.click();
        Assert.assertTrue(driver.getCurrentUrl().contains("estero"), "URL should update to Estero");
        System.out.println("Test 2: Estero Selected");
    }

    @Test(priority = 3, dependsOnMethods = "testSelectCity")
    public void testSelectSpecificStore() {
        WebElement specificStore = wait.until(ExpectedConditions.elementToBeClickable(
                By.partialLinkText("8018 Mediterranean Dr")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", specificStore);
        specificStore.click();

        // Wait for the specific store page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        Assert.assertTrue(driver.getPageSource().contains("33928"), "Zip code should be present");
        System.out.println("Test 3: Specific Store Page Loaded");
    }

    @Test(priority = 4, dependsOnMethods = "testSelectSpecificStore")
    public void testScrollInteraction() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll to bottom
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(1000); // Visual confirmation

        // Scroll to top
        js.executeScript("window.scrollTo(0, 0);");
        Thread.sleep(1000);

        System.out.println("Test 4: Scroll Interaction Completed");
    }

    @Test(priority = 5, dependsOnMethods = "testSelectSpecificStore")
    public void testVerifyStoreInfo() {
        // Verify that the Phone Number or Store Hours section is displayed
        WebElement phoneSection = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".LocationInfo-phone, .phone, [href^='tel:']")));

        Assert.assertTrue(phoneSection.isDisplayed(), "Phone number should be visible to users");
        System.out.println("Test 5: Store Information Verified");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}