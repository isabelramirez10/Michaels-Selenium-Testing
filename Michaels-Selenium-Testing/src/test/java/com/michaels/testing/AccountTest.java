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
    public void testFullLoginFlow() {
        getDriver().get(BASE_URL + "/account/login");
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));

        // 1. Enter Email
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email'], #email")));
        emailField.sendKeys(TEST_EMAIL);

        // 2. Click Continue (Common on modern sites)
        WebElement continueBtn = getDriver().findElement(By.cssSelector("button[type='submit']"));
        continueBtn.click();

        // 3. Enter Password
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='password'], #password")));
        passwordField.sendKeys(TEST_PASSWORD);

        // 4. Submit Login
        getDriver().findElement(By.cssSelector("button[type='submit']")).click();

        // 5. Verify successful login (Check for "Hi, Michael" or similar)
        WebElement welcomeText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".user-greeting, [class*='welcome']")));
        Assert.assertTrue(welcomeText.getText().contains(TEST_FIRST_NAME));
    }
}