package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

public class SearchTest extends BaseTest {

    private void performSearch(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        searchBox.clear();
        searchBox.sendKeys(keyword);
        searchBox.sendKeys(Keys.ENTER);
    }

    // 1. Search returns results for a valid keyword
    @Test
    public void testSearchForPaintReturnsResults() {
        performSearch("acrylic paint");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-item, .product-card, [data-testid='product']")));
        List<WebElement> results = driver.findElements(
                By.cssSelector(".product-item, .product-card, [data-testid='product']"));
        Assert.assertTrue(results.size() > 0,
                "Search for 'acrylic paint' should return at least one product.");
    }

    // 2. Search URL reflects the keyword typed
    @Test
    public void testSearchURLContainsKeyword() {
        performSearch("yarn");
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("yarn") ||
                        driver.getCurrentUrl().toLowerCase().contains("search"),
                "URL after search should reflect the search keyword.");
    }

    // 3. Search results load within 4 seconds
    @Test
    public void testSearchResultsLoadTime() {
        long start = System.currentTimeMillis();
        performSearch("canvas");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-item, .product-card, [data-testid='product']")));
        long elapsed = System.currentTimeMillis() - start;
        Assert.assertTrue(elapsed < 4000,
                "Search results should appear within 4 seconds. Took: " + elapsed + "ms");
    }

    // 4. Empty search does not crash the page
    @Test
    public void testEmptySearchDoesNotCrash() {
        WebElement searchBox = driver.findElement(
                By.cssSelector("input[type='search'], input[placeholder*='Search']"));
        searchBox.clear();
        searchBox.sendKeys(Keys.ENTER);
        Assert.assertFalse(driver.getCurrentUrl().isEmpty(),
                "Submitting an empty search should not crash the page.");
    }

    // 5. Search results page contains product prices
    @Test
    public void testSearchResultsContainPrices() {
        performSearch("brush");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".price, [class*='price'], [data-testid*='price']")));
        List<WebElement> prices = driver.findElements(
                By.cssSelector(".price, [class*='price'], [data-testid*='price']"));
        Assert.assertTrue(prices.size() > 0,
                "Search results should display prices for products.");
    }

    // 6. Search result images all have alt text
    @Test
    public void testSearchResultImagesHaveAltText() {
        performSearch("ribbon");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-item img, .product-card img")));
        List<WebElement> images = driver.findElements(
                By.cssSelector(".product-item img, .product-card img"));
        int missing = 0;
        for (WebElement img : images) {
            String alt = img.getAttribute("alt");
            if (alt == null || alt.trim().isEmpty()) missing++;
        }
        Assert.assertEquals(missing, 0,
                missing + " product image(s) in search results are missing alt text.");
    }

    // 7. Searching a nonsense keyword shows a no-results message
    @Test
    public void testNonsenseSearchShowsNoResultsMessage() {
        performSearch("xyzzy12345nonsense");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                bodyText.contains("no results") || bodyText.contains("0 results") ||
                        bodyText.contains("sorry") || bodyText.contains("found"),
                "Nonsense search should display a no-results or sorry message.");
    }

    // 8. Search field is cleared after navigating back to home
    @Test
    public void testSearchFieldClearsOnHomeReturn() {
        performSearch("glue");
        driver.navigate().back();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        String value = searchBox.getAttribute("value");
        Assert.assertTrue(value == null || value.trim().isEmpty(),
                "Search field should be empty after returning to the home page.");
    }
}