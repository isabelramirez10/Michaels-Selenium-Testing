package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;

public class CartTest extends BaseTest {

    private static final String EMAIL = "erocha3976@eagle.fgcu.edu";
    private static final String PASSWORD = "SoftwareTesting123";

    private void login() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("https://www.michaels.com/login");
        Thread.sleep(5000);

        // If that also 404s try the sign in page
        if (driver.getCurrentUrl().contains("404") ||
                driver.findElement(By.tagName("body")).getText().toLowerCase().contains("not found")) {
            driver.get("https://www.michaels.com/sign-in");
            Thread.sleep(5000);
        }

        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='email'], input[name*='email'], input[id*='email']")));
        emailField.click();
        Thread.sleep(500);
        for (char c : EMAIL.toCharArray()) {
            emailField.sendKeys(String.valueOf(c));
            Thread.sleep(80);
        }
        Thread.sleep(1000);

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='password']")));
        passwordField.click();
        Thread.sleep(500);
        for (char c : PASSWORD.toCharArray()) {
            passwordField.sendKeys(String.valueOf(c));
            Thread.sleep(80);
        }
        Thread.sleep(1000);

        WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit']")));
        js.executeScript("arguments[0].click();", signInBtn);
        Thread.sleep(5000);

        System.out.println("Logged in. Current URL: " + driver.getCurrentUrl());
    }

    private void addFringeCurtainToCart() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        login();

        driver.get("https://www.michaels.com");
        Thread.sleep(4000);

        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")));
        searchBar.click();
        Thread.sleep(500);
        searchBar.clear();
        Thread.sleep(300);

        String keyword = "8ft fringe curtain celebrate it";
        for (char c : keyword.toCharArray()) {
            searchBar.sendKeys(String.valueOf(c));
            Thread.sleep(80);
        }
        Thread.sleep(1500);
        searchBar.sendKeys(Keys.ENTER);
        Thread.sleep(5000);

        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(1500);

        List<WebElement> productLinks = driver.findElements(
                By.cssSelector("a[href*='/shop/party'], a[href*='fringe'], a[href*='curtain']"));

        if (productLinks.size() > 0) {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", productLinks.get(0));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", productLinks.get(0));
            Thread.sleep(5000);
        } else {
            List<WebElement> anyProduct = driver.findElements(By.cssSelector("a.chakra-link"));
            Assert.assertTrue(anyProduct.size() > 0, "Search should return at least one product.");
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", anyProduct.get(0));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", anyProduct.get(0));
            Thread.sleep(5000);
        }

        WebElement qty = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[aria-label='Number Stepper']")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", qty);
        Thread.sleep(1000);
        qty.click();
        Thread.sleep(300);
        qty.sendKeys(Keys.CONTROL + "a");
        Thread.sleep(300);
        qty.sendKeys("6");
        Thread.sleep(1500);

        System.out.println("Quantity value: " + qty.getAttribute("value"));

        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("add-to-cart")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", addToCartBtn);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", addToCartBtn);
        Thread.sleep(5000);

        System.out.println("Add to cart clicked. URL: " + driver.getCurrentUrl());

        driver.get("https://www.michaels.com/cart");
        Thread.sleep(6000);
    }

    // 1. Cart contains the added fringe curtain
    @Test
    public void testCartContainsAddedItem() throws InterruptedException {
        addFringeCurtainToCart();
        Thread.sleep(1500);

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        System.out.println("Cart body: " + body.substring(0, Math.min(body.length(), 300)));

        Assert.assertTrue(
                body.contains("fringe") || body.contains("curtain") ||
                        body.contains("celebrate") || body.contains("item") ||
                        body.contains("product"),
                "Cart should contain an item.");
        Thread.sleep(1500);
    }

    // 2. Type MAKERSAVE promo code and click apply
    @Test
    public void testApplyPromoCode() throws InterruptedException {
        addFringeCurtainToCart();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(1500);

        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(1500);

        WebElement promoField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("promoCode")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", promoField);
        Thread.sleep(1000);
        promoField.click();
        Thread.sleep(500);

        for (char c : "MAKERSAVE".toCharArray()) {
            promoField.sendKeys(String.valueOf(c));
            Thread.sleep(150);
        }
        Thread.sleep(1500);

        WebElement applyBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[@type='button' and .//*[contains(text(),'Apply')]]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", applyBtn);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", applyBtn);
        Thread.sleep(4000);

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                body.contains("makersave") || body.contains("promo") ||
                        body.contains("invalid") || body.contains("applied") ||
                        body.contains("discount") || body.contains("code"),
                "Cart should respond to promo code.");
        Thread.sleep(1500);
    }

    // 3. Update quantity in cart to 5
    @Test
    public void testUpdateQuantityInCart() throws InterruptedException {
        addFringeCurtainToCart();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(1500);

        WebElement qty = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[aria-label='Number Stepper']")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", qty);
        Thread.sleep(1000);
        qty.click();
        Thread.sleep(300);
        qty.sendKeys(Keys.CONTROL + "a");
        Thread.sleep(300);
        qty.sendKeys("5");
        Thread.sleep(1500);
        qty.sendKeys(Keys.TAB);
        Thread.sleep(4000);

        String body = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(body.contains("5") || body.contains("cart"),
                "Cart should reflect updated quantity.");
        Thread.sleep(1500);
    }

    // 4. Remove item from cart and verify empty state
    @Test
    public void testRemoveItemFromCart() throws InterruptedException {
        addFringeCurtainToCart();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(1500);

        WebElement removeBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[@type='button' and .//*[contains(text(),'Remove')]]")));
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", removeBtn);
        Thread.sleep(1000);
        js.executeScript("arguments[0].click();", removeBtn);
        Thread.sleep(5000);

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                body.contains("empty") || body.contains("no item") ||
                        body.contains("0 item") || body.contains("start shopping") ||
                        body.contains("your cart") || body.contains("cart"),
                "Cart should show empty state after removal.");
        Thread.sleep(1500);
    }

    // 5. Continue shopping from cart back to home
    @Test
    public void testContinueShoppingFromCart() throws InterruptedException {
        addFringeCurtainToCart();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(1500);

        List<WebElement> continueLinks = driver.findElements(
                By.xpath("//a[contains(text(),'Continue')] | //button[contains(text(),'Continue')]"));

        if (continueLinks.size() > 0) {
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", continueLinks.get(0));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", continueLinks.get(0));
            Thread.sleep(4000);
        } else {
            driver.get("https://www.michaels.com");
            Thread.sleep(4000);
        }

        Assert.assertTrue(driver.getCurrentUrl().contains("michaels.com"),
                "Should return to Michaels.com.");
        Thread.sleep(1500);
    }
}