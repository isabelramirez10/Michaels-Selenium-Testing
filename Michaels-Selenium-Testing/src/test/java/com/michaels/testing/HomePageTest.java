package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.Cookie;

public class HomePageTest extends BaseTest {

    // 1. Page title contains "Michaels"
    @Test
    public void testPageTitleContainsMichaels() {
        String title = driver.getTitle();
        Assert.assertTrue(title.toLowerCase().contains("michaels"),
                "Page title should contain 'Michaels'. Actual: " + title);
    }

    // 2. Logo is visible
    @Test
    public void testLogoIsDisplayed() {
        WebElement logo = driver.findElement(
                By.cssSelector("a[aria-label='Michaels'], .header-logo, img[alt*='Michaels']"));
        Assert.assertTrue(logo.isDisplayed(), "Logo should be visible on the home page.");
    }

    // 3. Search bar is present and accepts input
    @Test
    public void testSearchBarAcceptsInput() {
        WebElement searchBar = driver.findElement(
                By.cssSelector("input[type='search'], input[placeholder*='Search']"));
        searchBar.sendKeys("paint");
        Assert.assertEquals(searchBar.getAttribute("value"), "paint",
                "Search bar should accept and retain typed input.");
    }

    // 4. Page loads within 5 seconds
    @Test
    public void testHomePageLoadTime() {
        long start = System.currentTimeMillis();
        driver.get("https://www.michaels.com");
        long elapsed = System.currentTimeMillis() - start;
        Assert.assertTrue(elapsed < 5000,
                "Home page should load in under 5 seconds. Took: " + elapsed + "ms");
    }

    // 5. Site sets cookies on visit
    @Test
    public void testSiteSetsSessionCookies() {
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertFalse(cookies.isEmpty(),
                "Michaels.com should set at least one cookie on page visit.");
    }

    // 6. All images on the home page have alt attributes
    @Test
    public void testHomePageImagesHaveAltText() {
        List<WebElement> images = driver.findElements(By.tagName("img"));
        Assert.assertFalse(images.isEmpty(), "Home page should contain images.");
        int missingAlt = 0;
        for (WebElement img : images) {
            String alt = img.getAttribute("alt");
            if (alt == null || alt.trim().isEmpty()) {
                missingAlt++;
            }
        }
        Assert.assertEquals(missingAlt, 0,
                missingAlt + " image(s) are missing alt text on the home page.");
    }

    // 7. Page has exactly one H1 tag (accessibility)
    @Test
    public void testPageHasOneH1Tag() {
        List<WebElement> h1Tags = driver.findElements(By.tagName("h1"));
        Assert.assertEquals(h1Tags.size(), 1,
                "Home page should have exactly one H1 tag. Found: " + h1Tags.size());
    }

    // 8. Cart icon is visible and has aria-label
    @Test
    public void testCartIconHasAriaLabel() {
        WebElement cart = driver.findElement(
                By.cssSelector("[aria-label*='cart'], [aria-label*='Cart']"));
        String ariaLabel = cart.getAttribute("aria-label");
        Assert.assertNotNull(ariaLabel,
                "Cart icon should have an aria-label for accessibility.");
    }
}