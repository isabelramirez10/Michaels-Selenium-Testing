package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class AccountTest extends BaseTest {

    @Test
    public void testSignInLinkIsPresent() {
        WebElement signIn = driver.findElement(
                By.cssSelector("a[href*='login'], a[href*='signin'], a[aria-label*='Sign']"));
        Assert.assertTrue(signIn.isDisplayed(), "Sign In link should be present in the header.");
    }

    @Test
    public void testSignInPageLoads() {
        driver.get("https://www.michaels.com/account/login");
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("login") ||
                        driver.getCurrentUrl().toLowerCase().contains("account"),
                "Login page URL should be correct.");
    }

    @Test
    public void testEmailFieldPresent() {
        driver.get("https://www.michaels.com/account/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement email = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='email'], input[name*='email']")));
        Assert.assertTrue(email.isDisplayed(), "Email field should be displayed on the login page.");
    }

    @Test
    public void testPasswordFieldPresent() {
        driver.get("https://www.michaels.com/account/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement password = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='password']")));
        Assert.assertTrue(password.isDisplayed(), "Password field should be present on the login page.");
    }

    @Test
    public void testLoginButtonPresent() {
        driver.get("https://www.michaels.com/account/login");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("button[type='submit'], button[class*='login'], button[class*='sign']")));
        Assert.assertTrue(loginBtn.isDisplayed(), "Login/submit button should be visible.");
    }
}