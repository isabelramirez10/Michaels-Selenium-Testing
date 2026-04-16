package com.michaels.testing;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Collections;

public class CartTest extends BaseTest {

    private WebDriverWait getWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // --- FIX 1: Enhanced Popup Dismissal for 2026 ---
    private void dismissBlockingElements() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            // Force-remove common modal backdrops and popups that block clicks
            js.executeScript(
                    "var nodes = document.querySelectorAll('.modal-backdrop, .osano-cm-window, #onetrust-banner-sdk');" +
                            "for(var i=0; i<nodes.length; i++) { nodes[i].style.display='none'; }"
            );
        } catch (Exception ignored) {}
    }

    // Helper to get an item into the cart and reach the checkout page
    private void navigateToCheckout(String productSearch) {
        WebDriverWait wait = getWait();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("https://www.michaels.com");
        dismissBlockingElements();

        // Search
        WebElement search = wait.until(ExpectedConditions.elementToBeClickable(By.id("mainSearch")));
        search.sendKeys(productSearch + Keys.ENTER);

        // Click First Product
        WebElement product = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".product-tile-link")));
        js.executeScript("arguments[0].click();", product);

        // Adjust Qty to 3 (React-Safe Setter)
        WebElement qty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.quantity-select")));
        js.executeScript(
                "var setter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;" +
                        "setter.call(arguments[0], '3');" +
                        "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", qty);

        // Add to Cart
        WebElement addBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".add-to-cart")));
        addBtn.click();

        // Move to Cart Page via the Side Drawer
        WebElement viewCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/cart')] | //button[contains(., 'View Cart')]")));
        js.executeScript("arguments[0].click();", viewCartBtn);

        wait.until(ExpectedConditions.urlContains("/cart"));
    }

    @Test(description = "Verify adding 3 items to cart")
    public void test1_AddQuantityOfThree() {
        navigateToCheckout("canvas");
        WebElement qtyInput = getWait().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-item-quantity")));
        Assert.assertEquals(qtyInput.getAttribute("value"), "3");
    }

    @Test(description = "Verify promo code application")
    public void test2_ApplyPromoCode() {
        navigateToCheckout("brush");
        WebDriverWait wait = getWait();

        // Toggle promo section if it's collapsed
        try {
            driver.findElement(By.cssSelector(".promo-code-header")).click();
        } catch (Exception ignored) {}

        WebElement promoInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("couponCode")));
        promoInput.sendKeys("20MADEBYYOU");

        WebElement applyBtn = driver.findElement(By.cssSelector(".promo-code-submit"));
        applyBtn.click();

        // Assert that the page responded (either success message or 'invalid code' UI)
        WebElement feedback = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".coupon-message, .error-feedback")));
        Assert.assertTrue(feedback.isDisplayed());
    }

    @Test(description = "Update quantity directly in cart")
    public void test3_UpdateInCart() {
        navigateToCheckout("canvas");
        WebElement qty = getWait().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-item-quantity")));

        qty.clear();
        qty.sendKeys("2" + Keys.ENTER);

        getWait().until(ExpectedConditions.attributeToBe(qty, "value", "2"));
        Assert.assertEquals(qty.getAttribute("value"), "2");
    }

    @Test(description = "Remove item from cart")
    public void test4_RemoveItem() {
        navigateToCheckout("ribbon");
        WebElement remove = getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector(".remove-item-btn")));
        remove.click();

        WebElement empty = getWait().until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-empty")));
        Assert.assertTrue(empty.getText().contains("empty"));
    }

    @Test(description = "Navigate back to shopping from cart")
    public void test5_ContinueShopping() {
        navigateToCheckout("yarn");
        WebElement continueBtn = getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector(".continue-shopping")));
        continueBtn.click();

        Assert.assertFalse(driver.getCurrentUrl().contains("/cart"));
    }
}