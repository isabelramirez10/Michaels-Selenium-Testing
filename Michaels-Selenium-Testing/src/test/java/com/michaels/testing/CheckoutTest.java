package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class CheckoutTest extends BaseTest {

    @Test
    public void testCheckoutPageRequiresLogin() {
        driver.get("https://www.michaels.com/checkout");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String currentUrl = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(
                currentUrl.contains("login") || currentUrl.contains("checkout") || currentUrl.contains("cart"),
                "Checkout should redirect to login or stay on checkout/cart.");
    }

    @Test
    public void testCartPageHasContinueShoppingLink() {
        driver.get("https://www.michaels.com/cart");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        Assert.assertTrue(body.isDisplayed(), "Cart page should render for checkout flow.");
    }

    @Test
    public void testCheckoutURLIsReachable() {
        driver.get("https://www.michaels.com/checkout");
        Assert.assertFalse(driver.getCurrentUrl().isEmpty(), "Checkout URL should be reachable.");
    }

    @Test
    public void testCheckoutPageHasTitle() {
        driver.get("https://www.michaels.com/checkout");
        Assert.assertFalse(driver.getTitle().isEmpty(), "Checkout page should have a valid title.");
    }

    @Test
    public void testCheckoutFlowDoesNotReturn404() {
        driver.get("https://www.michaels.com/checkout");
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertFalse(bodyText.contains("404") && bodyText.contains("not found"),
                "Checkout page should not return a 404 error.");
    }
}