package com.michaels.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Collections;

public class AccountTest {
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    public WebDriver getDriver() { return driver.get(); }

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        // Helps bypass some bot detection by masking the "headless" or "automated" flags
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));

        WebDriver threadDriver = new ChromeDriver(options);
        threadDriver.manage().window().maximize();
        driver.set(threadDriver);
    }

    // A more robust wait that handles the "Interruption" screen
    public void navigateAndReady(String url) {
        getDriver().get(url);
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(45));

        // Wait for the body to at least exist
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        // If we see a challenge, we wait for the email field to appear
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        } catch (TimeoutException e) {
            System.out.println("Page load timed out - likely a bot challenge or slow network.");
        }
    }

    public void jsClick(WebElement element) {
        ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
    }

    @Test(priority = 1)
    public void testFullLoginFlow() {
        navigateAndReady("https://www.michaels.com/signin");
        getDriver().findElement(By.id("email")).sendKeys("erocha3976@eagle.fgcu.edu");
        getDriver().findElement(By.id("password")).sendKeys("SoftwareTesting123");

        WebElement loginBtn = getDriver().findElement(By.xpath("//button[@type='submit']"));
        jsClick(loginBtn); // Using JS click to bypass potential overlays
    }

    @Test(priority = 2)
    public void testInvalidPasswordError() {
        navigateAndReady("https://www.michaels.com/signin");
        getDriver().findElement(By.id("email")).sendKeys("erocha3976@eagle.fgcu.edu");
        getDriver().findElement(By.id("password")).sendKeys("WrongPassword123!");

        jsClick(getDriver().findElement(By.xpath("//button[@type='submit']")));

        // Broader search for ANY error text
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(20));
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Invalid') or contains(text(), 'incorrect') or contains(@class, 'error-message')]")));
        Assert.assertTrue(error.isDisplayed(), "Error message was not found on the page.");
    }

    @Test(priority = 3)
    public void testForgotPasswordNavigation() {
        navigateAndReady("https://www.michaels.com/signin");

        // Using a very flexible XPath for the 'Forgot' link/button
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        WebElement forgotLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(), 'Forgot')]")));
        jsClick(forgotLink);

        wait.until(ExpectedConditions.urlContains("forgot"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("forgot"));
    }

    @Test(priority = 4)
    public void testEmptyEmailValidation() {
        navigateAndReady("https://www.michaels.com/signin");

        jsClick(getDriver().findElement(By.xpath("//button[@type='submit']")));

        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'required') or contains(text(), 'enter')]")));
        Assert.assertTrue(emailError.isDisplayed());
    }

    @Test(priority = 5)
    public void testNavigationToSignup() {
        navigateAndReady("https://www.michaels.com/signin");

        // Michaels often changes this to "Create Account" or "Join"
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(15));
        WebElement signup = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href, 'signup')] | //*[contains(text(), 'Create')] | //*[contains(text(), 'Sign Up')]")));
        jsClick(signup);

        wait.until(ExpectedConditions.urlContains("signup"));
        Assert.assertTrue(getDriver().getCurrentUrl().contains("signup"));
    }

    @AfterMethod
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}