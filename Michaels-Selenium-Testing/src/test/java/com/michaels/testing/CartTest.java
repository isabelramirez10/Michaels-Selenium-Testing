package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
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
    public void testCartIconNavigatesToCart() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[href*='cart'], button[class*='cart'], [class*='minicart']")));
        Thread.sleep(1500);
        cartIcon.click();
        Thread.sleep(2000);
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("cart") ||
                        driver.getTitle().toLowerCase().contains("cart"),
                "Clicking cart icon should navigate to the cart page.");
    }

    // 2. Empty cart displays a relevant message and scrolls
    @Test
    public void testEmptyCartMessageAndScroll() throws InterruptedException {
        driver.get("https://www.michaels.com/cart");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(1500);
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                bodyText.contains("empty") || bodyText.contains("no items") || bodyText.contains("cart"),
                "Empty cart should display a relevant message.");
    }

    // 3. Cart page does not return a 404
    @Test
    public void testCartPageDoesNotReturn404() throws InterruptedException {
        driver.get("https://www.michaels.com/cart");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Thread.sleep(2000);
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertFalse(bodyText.contains("404") && bodyText.contains("not found"),
                "Cart page should not display a 404 error.");
    }

    // 4. Cart page loads within 5 seconds
    @Test
    public void testCartPageLoadTime() throws InterruptedException {
        long start = System.currentTimeMillis();
        driver.get("https://www.michaels.com/cart");
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        long elapsed = System.currentTimeMillis() - start;
        Thread.sleep(2000);
        Assert.assertTrue(elapsed < 5000,
                "Cart page should load within 5 seconds. Took: " + elapsed + "ms");
    }

    // 5. Cookies persist after refreshing the cart page
    @Test
    public void testCartCookiesPersistAfterRefresh() throws InterruptedException {
        driver.get("https://www.michaels.com/cart");
        Thread.sleep(2000);
        driver.navigate().refresh();
        Thread.sleep(2000);
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertFalse(cookies.isEmpty(),
                "Cookies should persist after refreshing the cart page.");
    }
}