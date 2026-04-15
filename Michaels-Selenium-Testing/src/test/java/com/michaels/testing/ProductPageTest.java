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

public class ProductPageTest extends BaseTest {

    private void navigateToProductPage() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        Thread.sleep(1500);
        searchBox.sendKeys("acrylic paint");
        Thread.sleep(1500);
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".product-item a, .product-card a, [data-testid='product'] a")));
        Thread.sleep(1000);
        firstProduct.click();
        Thread.sleep(2000);
    }

    // 1. Product page displays a non empty title
    @Test
    public void testProductPageHasTitle() throws InterruptedException {
        navigateToProductPage();
        WebElement title = driver.findElement(
                By.cssSelector("h1, .product-title, .product-name"));
        Thread.sleep(1500);
        Assert.assertTrue(title.isDisplayed(), "Product page should display a title.");
        Assert.assertFalse(title.getText().trim().isEmpty(),
                "Product title text should not be empty.");
    }

    // 2. Product price contains a dollar sign
    @Test
    public void testProductPriceContainsDollarSign() throws InterruptedException {
        navigateToProductPage();
        WebElement price = driver.findElement(
                By.cssSelector(".price, [class*='price'], [data-testid*='price']"));
        Thread.sleep(1500);
        Assert.assertTrue(price.isDisplayed(), "Price should be displayed.");
        Assert.assertTrue(price.getText().contains("$"),
                "Price should contain a dollar sign. Actual: " + price.getText());
    }

    // 3. Scroll down product page to reveal more details
    @Test
    public void testProductPageScrolling() throws InterruptedException {
        navigateToProductPage();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(1500);
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertTrue(body.isDisplayed(),
                "Product page should still be visible after scrolling.");
    }

    // 4. Hover over product image to check for zoom or interaction
    @Test
    public void testProductImageHoverInteraction() throws InterruptedException {
        navigateToProductPage();
        WebElement img = driver.findElement(
                By.cssSelector(".product-image img, [data-testid='product-image'] img, img[class*='product']"));
        Thread.sleep(1500);
        Actions actions = new Actions(driver);
        actions.moveToElement(img).perform();
        Thread.sleep(2000);
        String src = img.getAttribute("src");
        Assert.assertNotNull(src, "Product image should have a valid src after hover.");
        Assert.assertFalse(src.trim().isEmpty(),
                "Product image src should not be empty after hover.");
    }

    // 5. Back button returns to search results
    @Test
    public void testBackButtonReturnsToSearchResults() throws InterruptedException {
        navigateToProductPage();
        Thread.sleep(1500);
        driver.navigate().back();
        Thread.sleep(2000);
        String url = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(url.contains("search") || url.contains("paint"),
                "Back button should return to the search results page.");
    }
}