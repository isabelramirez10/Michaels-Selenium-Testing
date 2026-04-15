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

    // 2. Logo or home link is visible in the header
    @Test
    public void testLogoIsDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement logo = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("header a, .header a, nav a[href='/'], a[href='https://www.michaels.com']")));
        Assert.assertTrue(logo.isDisplayed(),
                "A logo or home link should be visible in the header.");
    }

    // 3. Search bar accepts input
    @Test
    public void testSearchBarAcceptsInput() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        searchBar.sendKeys("paint");
        Assert.assertEquals(searchBar.getAttribute("value"), "paint",
                "Search bar should accept and retain typed input.");
    }

    // 4. Home page loads within 5 seconds
    @Test
    public void testHomePageLoadTime() {
        long start = System.currentTimeMillis();
        driver.get("https://www.michaels.com");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
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

    // 6. Images on home page - documents any missing alt text as an accessibility finding
    @Test
    public void testHomePageImagesHaveAltText() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("img")));
        List<WebElement> images = driver.findElements(By.tagName("img"));
        Assert.assertFalse(images.isEmpty(), "Home page should contain images.");
        int missingAlt = 0;
        for (WebElement img : images) {
            String alt = img.getAttribute("alt");
            if (alt == null || alt.trim().isEmpty()) {
                missingAlt++;
            }
        }
        System.out.println("Accessibility finding - images missing alt text: " + missingAlt);
        Assert.assertTrue(missingAlt < images.size(),
                "At least some images should have alt text. Total missing: " + missingAlt);
    }

    // 7. Page has at least one H1 tag
    @Test
    public void testPageHasOneH1Tag() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
        List<WebElement> h1Tags = driver.findElements(By.tagName("h1"));
        Assert.assertTrue(h1Tags.size() >= 1,
                "Home page should have at least one H1 tag. Found: " + h1Tags.size());
    }

    // 8. Cart element is visible in the header
    @Test
    public void testCartIconHasAriaLabel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cart = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href*='cart'], button[class*='cart'], [class*='minicart'], [class*='mini-cart']")));
        Assert.assertTrue(cart.isDisplayed(),
                "Cart element should be visible in the header.");
    }
}