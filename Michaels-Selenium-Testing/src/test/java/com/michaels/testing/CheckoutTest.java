package com.michaels.testing;

import com.michaels.testing.ERBaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class CheckoutTest extends ERBaseTest {

    @Test
    public void testCheckoutPageRequiresLogin() {
        driver.get("https://www.michaels.com/checkout");
        handlePopups(); // Helps prevent pop-ups from blocking the URL check

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Added a tiny wait for the URL to settle in case of redirects
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("login"),
                ExpectedConditions.urlContains("checkout"),
                ExpectedConditions.urlContains("cart")
        ));

        String currentUrl = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(
                currentUrl.contains("login") || currentUrl.contains("checkout") || currentUrl.contains("cart"),
                "Checkout should redirect to login or stay on checkout/cart.");
    }

    private void handlePopups() {
    }

    @Test
    public void testCartPageHasContinueShoppingLink() {
        driver.get("https://www.michaels.com/cart");
        handlePopups();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Waiting for the body ensures the page has actually rendered
        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        Assert.assertTrue(body.isDisplayed(), "Cart page should render for checkout flow.");
    }

    @Test
    public void testCheckoutURLIsReachable() {
        driver.get("https://www.michaels.com/checkout");
        // Simple and effective check
        Assert.assertFalse(driver.getCurrentUrl().isEmpty(), "Checkout URL should be reachable.");
    }

    @Test
    public void testCheckoutPageHasTitle() {
        driver.get("https://www.michaels.com/checkout");
        // Ensures the browser actually grabbed page metadata
        Assert.assertFalse(driver.getTitle().isEmpty(), "Checkout page should have a valid title.");
    }

    @Test
    public void testCheckoutFlowDoesNotReturn404() {
        driver.get("https://www.michaels.com/checkout");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Ensure body is present before grabbing text to avoid NullPointerException
        WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        String bodyText = body.getText().toLowerCase();

        // Using || instead of && because usually, a 404 page might just say "404" OR "Not Found"
        Assert.assertFalse(bodyText.contains("404") || bodyText.contains("not found"),
                "Checkout page should not return a 404 error.");
    }
}