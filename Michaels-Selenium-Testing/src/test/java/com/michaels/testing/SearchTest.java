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

    private void typeInSearchBar(String keyword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")
        ));

        searchBar.click();
        searchBar.clear();
        searchBar.sendKeys(keyword);
        searchBar.sendKeys(Keys.ENTER);

        // Wait for results page to load
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href*='/product']")
        ));
    }

    // 1. Search for acrylic paint, scroll, click first product
    @Test
    public void testSearchThenClickProduct() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        typeInSearchBar("acrylic paint");

        List<WebElement> products = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(
                        By.cssSelector("a[href*='/product']")
                )
        );

        Assert.assertTrue(products.size() > 0, "No product links found.");

        WebElement firstProduct = products.get(0);

        js.executeScript("arguments[0].scrollIntoView(true);", firstProduct);
        wait.until(ExpectedConditions.elementToBeClickable(firstProduct));

        js.executeScript("arguments[0].click();", firstProduct);

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("search")
        ));

        Assert.assertFalse(driver.getCurrentUrl().contains("search"),
                "Should navigate to product page.");
    }

    // 2. Search for yarn and sort by price low to high

    @Test
    public void testSearchAndSort() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        typeInSearchBar("yarn");
        Thread.sleep(2000);

        // Click the sort button using visible text
        WebElement sortButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Sort')] | //button[.//*[contains(text(),'Sort')]] | //button[contains(@class,'sort')]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", sortButton);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", sortButton);
        Thread.sleep(2000);

        // Click Low to High option
        WebElement lowToHigh = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(),'Low to High') or contains(text(),'low to high') or contains(text(),'Price: Low')]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", lowToHigh);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", lowToHigh);
        Thread.sleep(3000);

        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("a[href*='/product']")));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("yarn") ||
                        driver.getCurrentUrl().toLowerCase().contains("search"),
                "Should remain on yarn results after sorting.");
        Thread.sleep(1000);
    }

    // 3. Search for canvas and scroll to bottom properly
    @Test
    public void testSearchScrollAndPagination() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        typeInSearchBar("canvas");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href*='/product']")
        ));


        long lastHeight = (long) js.executeScript("return document.body.scrollHeight");
        while (true) {
            for (long currentScroll = 0; currentScroll < lastHeight; currentScroll += 300) {
                js.executeScript("window.scrollBy(0, 300);");
                Thread.sleep(400); // slower, visible scrolling
            }

            Thread.sleep(2000); // allow new products to load

            long newHeight = (long) js.executeScript("return document.body.scrollHeight");
            if (newHeight == lastHeight) {
                break;
            }
            lastHeight = newHeight;
        }

        // Close the page when scrolling reaches the bottom
        driver.close();
    }

    // 4. Type in search bar, view search history suggestions, then clear search history
    @Test
    public void testClearSearchHistory() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Perform a real search to create history
        typeInSearchBar("paint");
        Thread.sleep(2000);

        // Step 2: Click search bar again
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")));
        searchBar.click();
        Thread.sleep(2000);

        // Step 3: Clear text using BACKSPACE (more realistic than clear())
        String existingText = searchBar.getAttribute("value");
        for (int i = 0; i < existingText.length(); i++) {
            searchBar.sendKeys(Keys.BACK_SPACE);
        }
        Thread.sleep(1500);

        // Step 4: Click again to ensure dropdown appears
        searchBar.click();
        Thread.sleep(2000);

        // Step 5: Click "Clear Search History" using TEXT (more stable)
        WebElement clearBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[.//p[contains(text(),'Clear')]]")));

        js.executeScript("arguments[0].scrollIntoView({block:'center'});", clearBtn);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", clearBtn);
        Thread.sleep(3000);

        // Step 6: Verify history is gone visually
        searchBar.click();
        Thread.sleep(2000);

        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();

        System.out.println("Body text after clearing history: " + bodyText);

    }

    // 5. Nonsense search then recover
    @Test
    public void testNonsenseSearchThenRecovery() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        typeInSearchBar("xyzzy12345nonsense");

        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();

        System.out.println("Body text after nonsense search: " + bodyText);


        Thread.sleep(3000);

        // Pause to see the no results screen
        Thread.sleep(3000);

        // Search again
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")
        ));

        searchBar.click();
        searchBar.clear();
        searchBar.sendKeys("brush");
        searchBar.sendKeys(Keys.ENTER);

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("a[href*='/product']")
        ));



    }

}