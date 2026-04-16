package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.time.Duration;

public class StoreLocatorTest extends BaseTest {

    // Helper method to clear popups/banners
    private void handlePopups() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            // Common Michaels cookie/promo close button selectors
            By closeButton = By.cssSelector("button[aria-label='Close'], .close-button, #onetrust-accept-btn-handler");
            List<WebElement> buttons = driver.findElements(closeButton);
            if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
                buttons.get(0).click();
                System.out.println("Banner dismissed.");
            }
        } catch (Exception e) {
            // If no banner is found, just move on
        }
    }

    @Test(priority = 1)
    public void testStoreLocatorPageLoads() {
        driver.get("https://www.michaels.com/store-locator");
        handlePopups();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Assert.assertTrue(driver.getCurrentUrl().contains("michaels.com"));
    }

    @Test(priority = 2)
    public void testStoreLocatorHasSearchInput() {
        driver.get("https://www.michaels.com/store-locator");
        handlePopups(); // Clear the path!

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Using a more generic search for the input on the locations subdomain
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@class, 'input') or @id='q' or @type='text']")));

        Assert.assertTrue(input.isDisplayed(), "Search input should be visible and not blocked.");
    }

    @Test(priority = 3)
    public void testStoreSearchWithValidZip() {
        driver.get("https://www.michaels.com/store-locator");
        handlePopups();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // 1. Michaels uses an iframe for their store locator map.
        // We have to wait for the frame to exist and then switch into it.
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.cssSelector("iframe[title*='Store Locator'], .store-locator-iframe, iframe#yext-iframe-1")));

        // 2. Now that we are INSIDE the frame, we find the search box.
        // The ID 'q' is standard inside this specific frame.
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("q")));

        input.clear();
        input.sendKeys("33967");
        input.sendKeys(Keys.ENTER);

        // 3. Wait for the list of stores to appear inside the frame
        WebElement results = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".LocationList, .js-location-list, .lp-results")));

        Assert.assertTrue(results.isDisplayed(), "Store locations should be listed within the frame.");

        // 4. CRITICAL: Switch back to the main page so the next tests don't fail!
        driver.switchTo().defaultContent();
    }

    @Test(priority = 4)
    public void testStoreLocatorPageTitle() {
        driver.get("https://www.michaels.com/store-locator");
        Assert.assertFalse(driver.getTitle().isEmpty());
    }

    @Test(priority = 5)
    public void testStoreLocatorBodyIsVisible() {
        driver.get("https://www.michaels.com/store-locator");
        Assert.assertTrue(driver.findElement(By.tagName("body")).isDisplayed());
    }
}