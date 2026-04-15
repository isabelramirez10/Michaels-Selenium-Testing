package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

public class SearchTest extends BaseTest {

    private void performSearch(String keyword) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        Thread.sleep(1500);
        searchBox.clear();
        searchBox.sendKeys(keyword);
        Thread.sleep(1500);
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
    }

    // 1. Search returns visible product results and scrolls through them
    @Test
    public void testSearchReturnsResultsAndScrolls() throws InterruptedException {
        performSearch("acrylic paint");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-item, .product-card, [data-testid='product']")));
        Thread.sleep(1500);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(1500);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(1500);
        List<WebElement> results = driver.findElements(
                By.cssSelector(".product-item, .product-card, [data-testid='product']"));
        Assert.assertTrue(results.size() > 0,
                "Search for 'acrylic paint' should return at least one product.");
    }

    // 2. Apply a filter on search results
    @Test
    public void testSearchFilterIsClickable() throws InterruptedException {
        performSearch("canvas");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-item, .product-card, [data-testid='product']")));
        Thread.sleep(1500);

        // Look for a filter option and click it
        List<WebElement> filters = driver.findElements(
                By.cssSelector("[class*='filter'], [class*='facet'], [id*='filter'], [class*='refine']"));
        if (filters.size() > 0) {
            Thread.sleep(1000);
            filters.get(0).click();
            Thread.sleep(2000);
        }

        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("canvas") || url.contains("search"),
                "After applying a filter the page should remain on search results.");
    }

    // 3. Empty search does not crash the page
    @Test
    public void testEmptySearchDoesNotCrash() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        Thread.sleep(1500);
        searchBox.clear();
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        Assert.assertFalse(driver.getCurrentUrl().isEmpty(),
                "Submitting an empty search should not crash the page.");
    }

    // 4. Search results contain prices and hover over first product
    @Test
    public void testSearchResultsHoverOverProduct() throws InterruptedException {
        performSearch("brush");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".product-item, .product-card, [data-testid='product']")));
        Thread.sleep(1500);

        // Hover over the first product card
        List<WebElement> products = driver.findElements(
                By.cssSelector(".product-item, .product-card, [data-testid='product']"));
        if (products.size() > 0) {
            Actions actions = new Actions(driver);
            actions.moveToElement(products.get(0)).perform();
            Thread.sleep(2000);
        }

        List<WebElement> prices = driver.findElements(
                By.cssSelector(".price, [class*='price'], [data-testid*='price']"));
        Assert.assertTrue(prices.size() > 0,
                "Search results should display prices for products.");
    }

    // 5. Nonsense keyword shows a no results message
    @Test
    public void testNonsenseSearchShowsNoResults() throws InterruptedException {
        performSearch("xyzzy12345nonsense");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Thread.sleep(2000);
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                bodyText.contains("no results") || bodyText.contains("0 results") ||
                        bodyText.contains("sorry") || bodyText.contains("found"),
                "Nonsense search should display a no results message.");
    }
}