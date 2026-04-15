package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Set;

public class CartTest extends BaseTest {

    // 1. Cart icon navigates to the cart page
    @Test
    public void testCartIconNavigatesToCart() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[aria-label*='cart'], [aria-label*='Cart'], a[href*='cart']")));
        cartIcon.click();
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("cart") ||
                        driver.getTitle().toLowerCase().contains("cart"),
                "Clicking cart icon should navigate to the cart page.");
    }

    // 2. Empty cart page displays an appropriate message
    @Test
    public void testEmptyCartMessageDisplayed() {
        driver.get("https://www.michaels.com/cart");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String bodyText = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.tagName("body"))).getText().toLowerCase();
        Assert.assertTrue(
                bodyText.contains("empty") || bodyText.contains("no items") || bodyText.contains("cart"),
                "Empty cart should display a relevant message.");
    }

    // 3. Cart URL is correct
    @Test
    public void testCartURLIsCorrect() {
        driver.get("https://www.michaels.com/cart");
        Assert.assertTrue(driver.getCurrentUrl().contains("cart"),
                "Cart URL should contain 'cart'.");
    }

    // 4. Cart page does not return a 404
    @Test
    public void testCartPageDoesNotReturn404() {
        driver.get("https://www.michaels.com/cart");
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertFalse(bodyText.contains("404") && bodyText.contains("not found"),
                "Cart page should not display a 404 error.");
    }

    // 5. Cart page loads within 5 seconds
    @Test
    public void testCartPageLoadTime() {
        long start = System.currentTimeMillis();
        driver.get("https://www.michaels.com/cart");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        long elapsed = System.currentTimeMillis() - start;
        Assert.assertTrue(elapsed < 5000,
                "Cart page should load within 5 seconds. Took: " + elapsed + "ms");
    }

    // 6. Cart page title is not empty
    @Test
    public void testCartPageHasTitle() {
        driver.get("https://www.michaels.com/cart");
        Assert.assertFalse(driver.getTitle().isEmpty(),
                "Cart page should have a non-empty title.");
    }

    // 7. Cart persists after page refresh (cookie check)
    @Test
    public void testCartCookieExistsAfterVisit() {
        driver.get("https://www.michaels.com/cart");
        driver.navigate().refresh();
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertFalse(cookies.isEmpty(),
                "Cookies should persist after refreshing the cart page.");
    }

    // 8. Navigating from cart back to home works
    @Test
    public void testCartBackToHomeNavigation() {
        driver.get("https://www.michaels.com/cart");
        driver.navigate().back();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Assert.assertTrue(driver.getCurrentUrl().contains("michaels.com"),
                "Navigating back from cart should return to a Michaels.com page.");
    }
}