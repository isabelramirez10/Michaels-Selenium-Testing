package com.michaels.testing;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

public class StoreLocatorTest extends ERBaseTest {

    // Helper: dismiss cookie/promo banners
    private void handlePopups() {
        try {
            By closeBtn = By.cssSelector(
                    "button[aria-label='Close'], .close-button, #onetrust-accept-btn-handler");
            List<WebElement> buttons = driver.findElements(closeBtn);
            if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
                buttons.get(0).click();
                System.out.println("Banner dismissed.");
            }
        } catch (Exception ignored) {}
    }

    @Test(priority = 1)
    public void testStoreLocatorPageLoads() {
        driver.get(BASE_URL + "/store-locator");
        handlePopups();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // Assert actual page URL is correct
        Assert.assertTrue(driver.getCurrentUrl().contains("michaels.com"),
                "Store locator should be on the Michaels domain.");

        // Assert page title is meaningful
        String title = driver.getTitle();
        Assert.assertFalse(title.isEmpty(), "Page title should not be empty.");
        System.out.println("Page title: " + title);
    }

    @Test(priority = 2)
    public void testStoreLocatorContainsIframe() {
        driver.get(BASE_URL + "/store-locator");

        // Aggressively handle popups first
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#onetrust-accept-btn-handler, button[aria-label='Close']," +
                            " .cookie-accept, .accept-cookies")));
            acceptBtn.click();
            System.out.println("Cookie popup dismissed.");
        } catch (TimeoutException ignored) {
            System.out.println("No cookie popup found.");
        }

        // Give the iframe time to fully render after popup clears
        try { Thread.sleep(4000); } catch (InterruptedException ignored) {}

        // Wait for at least one iframe to appear in the DOM
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

        Assert.assertFalse(iframes.isEmpty(),
                "Store locator page should contain at least one iframe element.");

        // Find the first iframe with actual visible dimensions
        WebElement visibleIframe = null;
        for (WebElement iframe : iframes) {
            int width = iframe.getSize().getWidth();
            int height = iframe.getSize().getHeight();
            System.out.println("Iframe found — size: " + width + "x" + height);
            if (width > 0 && height > 0) {
                visibleIframe = iframe;
                break;
            }
        }

        Assert.assertNotNull(visibleIframe,
                "At least one iframe should have a visible (non-zero) size.");
        Assert.assertTrue(visibleIframe.getSize().getWidth() > 0,
                "Store locator iframe should have a visible width.");
        Assert.assertTrue(visibleIframe.getSize().getHeight() > 0,
                "Store locator iframe should have a visible height.");
    }

    @Test(priority = 3)
    public void testStoreLocatorPageHasHeading() {
        driver.get(BASE_URL + "/store-locator");
        handlePopups();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Verify the page renders a heading (h1 or h2) related to store finding
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1 | //h2")));

        Assert.assertTrue(heading.isDisplayed(),
                "Store locator page should have a visible heading.");

        String headingText = heading.getText().toLowerCase();
        System.out.println("Heading found: " + heading.getText());

        // Verify the heading is relevant to store finding
        Assert.assertTrue(
                headingText.contains("store") || headingText.contains("location")
                        || headingText.contains("find") || headingText.contains("near"),
                "Heading should relate to store/location finding. Found: " + heading.getText());
    }

    @Test(priority = 4)
    public void testStoreLocatorPageTitle() {
        driver.get(BASE_URL + "/store-locator");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String title = driver.getTitle();
        Assert.assertFalse(title.isEmpty(),
                "Store locator page should have a non-empty title.");

        // Verify the title is relevant
        Assert.assertTrue(
                title.toLowerCase().contains("store") || title.toLowerCase().contains("michaels"),
                "Title should reference store or Michaels. Got: " + title);
    }

    @Test(priority = 5)
    public void testStoreLocatorBodyIsVisible() {
        driver.get(BASE_URL + "/store-locator");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Verify body is present and rendered
        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("body")));
        Assert.assertTrue(body.isDisplayed(),
                "The body of the store locator page should be visible.");

        // Also verify the page has meaningful content — not a blank/error page
        String bodyText = body.getText();
        Assert.assertFalse(bodyText.trim().isEmpty(),
                "Page body should contain visible text content.");
    }
}