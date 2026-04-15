package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class ProductPageTest extends BaseTest {

    private void navigateToProductPage() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        searchBox.sendKeys("acrylic paint");
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".product-item a, .product-card a, [data-testid='product'] a")));
        firstProduct.click();
        Thread.sleep(2000);
    }

    // 1. Product page has a title (H1)
    @Test
    public void testProductPageHasTitle() throws InterruptedException {
        navigateToProductPage();
        WebElement title = driver.findElement(By.cssSelector("h1, .product-title, .product-name"));
        Assert.assertTrue(title.isDisplayed(), "Product page should display a product title.");
        Assert.assertFalse(title.getText().trim().isEmpty(),
                "Product title text should not be empty.");
    }

    // 2. Product price is displayed and contains a dollar sign
    @Test
    public void testProductPriceContainsDollarSign() throws InterruptedException {
        navigateToProductPage();
        WebElement price = driver.findElement(
                By.cssSelector(".price, [class*='price'], [data-testid*='price']"));
        Assert.assertTrue(price.isDisplayed(), "Price should be displayed.");
        Assert.assertTrue(price.getText().contains("$"),
                "Price should contain a dollar sign. Actual: " + price.getText());
    }

    // 3. Product image has a valid, non-empty src
    @Test
    public void testProductImageHasValidSrc() throws InterruptedException {
        navigateToProductPage();
        WebElement img = driver.findElement(
                By.cssSelector(".product-image img, [data-testid='product-image'] img"));
        String src = img.getAttribute("src");
        Assert.assertNotNull(src, "Product image should have a src attribute.");
        Assert.assertFalse(src.trim().isEmpty(), "Product image src should not be empty.");
    }

    // 4. Product image has alt text
    @Test
    public void testProductImageHasAltText() throws InterruptedException {
        navigateToProductPage();
        WebElement img = driver.findElement(
                By.cssSelector(".product-image img, [data-testid='product-image'] img"));
        String alt = img.getAttribute("alt");
        Assert.assertNotNull(alt, "Product image should have an alt attribute.");
        Assert.assertFalse(alt.trim().isEmpty(), "Product image alt text should not be empty.");
    }

    // 5. Add to Cart button is enabled (not disabled)
    @Test
    public void testAddToCartButtonIsEnabled() throws InterruptedException {
        navigateToProductPage();
        WebElement addToCart = driver.findElement(
                By.cssSelector("button[class*='cart'], button[aria-label*='cart']"));
        Assert.assertTrue(addToCart.isEnabled(),
                "Add to Cart button should be enabled on the product page.");
    }

    // 6. Product URL is different from the homepage URL
    @Test
    public void testProductURLDiffersFromHomepage() throws InterruptedException {
        String homeURL = driver.getCurrentUrl();
        navigateToProductPage();
        Assert.assertNotEquals(driver.getCurrentUrl(), homeURL,
                "Product page URL should differ from the homepage.");
    }

    // 7. Product page loads within 5 seconds
    @Test
    public void testProductPageLoadTime() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        searchBox.sendKeys("acrylic paint");
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        WebElement firstProduct = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".product-item a, .product-card a")));
        long start = System.currentTimeMillis();
        firstProduct.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1")));
        long elapsed = System.currentTimeMillis() - start;
        Assert.assertTrue(elapsed < 5000,
                "Product page should load within 5 seconds. Took: " + elapsed + "ms");
    }

    // 8. Browser back button returns to search results
    @Test
    public void testBackButtonReturnsToSearchResults() throws InterruptedException {
        navigateToProductPage();
        driver.navigate().back();
        Thread.sleep(1500);
        String url = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(url.contains("search") || url.contains("paint"),
                "Back button should return to the search results page.");
    }
}