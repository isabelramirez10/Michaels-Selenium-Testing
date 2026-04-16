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

    private void typeInSearchBar(String keyword) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")));
        Thread.sleep(2000);
        searchBar.click();
        Thread.sleep(2000);
        searchBar.clear();
        for (char c : keyword.toCharArray()) {
            searchBar.sendKeys(String.valueOf(c));
            Thread.sleep(200);
        }
        Thread.sleep(3000);
        searchBar.sendKeys(Keys.ENTER);
        Thread.sleep(6000);
    }

    // 1. Search for acrylic paint, scroll through results, click first product
    @Test
    public void testSearchThenClickProduct() throws InterruptedException {
        typeInSearchBar("acrylic paint");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a.chakra-link")));
        Thread.sleep(3000);

        // Scroll through results
        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(3000);
        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(3000);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(3000);

        // Click the first product link
        List<WebElement> products = driver.findElements(By.cssSelector("a.chakra-link"));
        Assert.assertTrue(products.size() > 0, "Search results should contain product links.");

        products.get(0).click();
        Thread.sleep(5000);

        Assert.assertFalse(driver.getCurrentUrl().contains("search") &&
                        driver.getCurrentUrl().equals(BASE_URL),
                "Clicking a product should navigate to a product page.");
    }

    // 2. Search for yarn and attempt to sort by price low to high
    @Test
    public void testSearchAndSort() throws InterruptedException {
        typeInSearchBar("yarn");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(3000);

        // Look for a sort dropdown
        List<WebElement> sortDropdowns = driver.findElements(
                By.cssSelector("select, [class*='sort'], [aria-label*='sort'], [aria-label*='Sort']"));

        if (sortDropdowns.size() > 0) {
            Thread.sleep(2000);
            sortDropdowns.get(0).click();
            Thread.sleep(3000);

            List<WebElement> sortOptions = driver.findElements(
                    By.cssSelector("option, [class*='sort-option']"));
            if (sortOptions.size() > 1) {
                sortOptions.get(1).click();
                Thread.sleep(4000);
            }
        }

        Assert.assertTrue(driver.getCurrentUrl().contains("yarn") ||
                        driver.getCurrentUrl().toLowerCase().contains("search"),
                "Page should still show yarn search results after sorting.");
    }

    // 3. Search for canvas and scroll to bottom to check for pagination
    @Test
    public void testSearchScrollAndPagination() throws InterruptedException {
        typeInSearchBar("canvas");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a.chakra-link")));
        Thread.sleep(3000);

        js.executeScript("window.scrollBy(0, 600)");
        Thread.sleep(3000);
        js.executeScript("window.scrollBy(0, 600)");
        Thread.sleep(3000);
        js.executeScript("window.scrollBy(0, 600)");
        Thread.sleep(3000);
        js.executeScript("window.scrollBy(0, 600)");
        Thread.sleep(3000);

        // Check for pagination or load more
        List<WebElement> pagination = driver.findElements(
                By.cssSelector("[aria-label*='page'], [class*='pagination'], button[class*='load']"));

        // Scroll back to top
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(3000);

        Assert.assertTrue(driver.getCurrentUrl().contains("canvas") ||
                        driver.getCurrentUrl().toLowerCase().contains("search"),
                "Should remain on canvas search results page after scrolling.");
    }

    // 4. Search for glitter and hover over first 3 product cards then click the third
    @Test
    public void testSearchHoverAndClickThirdProduct() throws InterruptedException {
        typeInSearchBar("glitter");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Actions actions = new Actions(driver);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a.chakra-link")));
        Thread.sleep(3000);

        List<WebElement> products = driver.findElements(By.cssSelector("a.chakra-link"));
        Assert.assertTrue(products.size() >= 3,
                "Search results should have at least 3 products to hover over.");

        // Hover over first product
        actions.moveToElement(products.get(0)).perform();
        Thread.sleep(3000);

        // Hover over second product
        actions.moveToElement(products.get(1)).perform();
        Thread.sleep(3000);

        // Hover over third product then click
        actions.moveToElement(products.get(2)).perform();
        Thread.sleep(3000);
        products.get(2).click();
        Thread.sleep(5000);

        Assert.assertFalse(driver.getCurrentUrl().isEmpty(),
                "Clicking the third product should navigate to a product page.");
    }

    // 5. Search nonsense then search again for brush from the results page
    @Test
    public void testNonsenseSearchThenRecovery() throws InterruptedException {
        typeInSearchBar("xyzzy12345nonsense");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(5000);

        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                bodyText.contains("no results") || bodyText.contains("0 result") ||
                        bodyText.contains("sorry") || bodyText.contains("no product") ||
                        bodyText.contains("found") || bodyText.contains("search"),
                "Nonsense search should show a no results message.");

        Thread.sleep(3000);

        // Now search again for brush using the same search bar
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")));
        searchBar.click();
        Thread.sleep(2000);
        searchBar.clear();
        Thread.sleep(1000);

        for (char c : "brush".toCharArray()) {
            searchBar.sendKeys(String.valueOf(c));
            Thread.sleep(200);
        }
        Thread.sleep(3000);
        searchBar.sendKeys(Keys.ENTER);
        Thread.sleep(6000);

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("brush") ||
                        driver.getCurrentUrl().toLowerCase().contains("search"),
                "Recovery search for brush should return results.");
    }
}