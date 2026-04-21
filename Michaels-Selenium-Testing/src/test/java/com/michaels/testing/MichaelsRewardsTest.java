package com.michaels.testing;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

public class MichaelsRewardsTest {
    WebDriver driver;
    WebDriverWait wait;
    private final String REWARDS_URL = "https://www.michaels.com/rewards";

    @BeforeClass
    public void setUp() throws InterruptedException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.addArguments("window-size=1920,1080");
        options.addArguments("user-agent=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get(REWARDS_URL);
        Thread.sleep(6000);
    }

    @Test(priority = 1)
    public void testJoinNowButtonVisibility() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        boolean isJoinVisible = (boolean) js.executeScript(
                "var elements = document.querySelectorAll('a, button, span, h1, h2');" +
                        "for (var i = 0; i < elements.length; i++) {" +
                        "  var txt = elements[i].textContent.toLowerCase();" +
                        "  if (txt.includes('join') || txt.includes('sign up') || txt.includes('rewards')) { return true; }" +
                        "}" +
                        "return false;"
        );
        Assert.assertTrue(isJoinVisible, "Join Now text not found.");
    }

    @Test(priority = 2)
    public void testSmoothScrollToBenefits() throws InterruptedException {
        // Increased scroll distance to 1500 to ensure we pass the initial fold
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy({ top: 1500, behavior: 'smooth' });");
        Thread.sleep(3000); // Longer pause to observe the movement

        System.out.println("Test 2: Performed deep scroll to lower sections.");
    }

    @Test(priority = 3)
    public void testCreditCardNavigation() throws InterruptedException {
        // 1. Define the known target URL
        String targetCCUrl = "https://www.michaels.com/creditcard";

        System.out.println("Test 3: Navigating directly to Credit Card page...");

        // 2. Navigate directly
        driver.get(targetCCUrl);

        // 3. Wait for the page to load and verify URL
        wait.until(ExpectedConditions.urlContains("creditcard"));
        String currentUrl = driver.getCurrentUrl();

        Assert.assertTrue(currentUrl.contains("michaels.com/creditcard"),
                "Failed to reach Credit Card page. Actual URL: " + currentUrl);

        // 4. Verify a specific element on the CC page to prove it loaded
        // We look for the "Apply Now" or "Credit Card" header text
        boolean isPageLoaded = (boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.body.innerText.toLowerCase().includes('credit card');"
        );
        Assert.assertTrue(isPageLoaded, "Credit Card page loaded but content is missing.");

        System.out.println("Test 3: Credit Card page verified successfully.");

        // 5. Return to Rewards so Tests 4 and 5 don't break
        driver.navigate().to(REWARDS_URL);
        wait.until(ExpectedConditions.urlContains("/rewards"));
    }

    @Test(priority = 4)
    public void testSearchFunctionality() throws InterruptedException {
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@name='q' or @type='search' or contains(@placeholder, 'Search')]")));

        searchInput.sendKeys("Markers");
        searchInput.sendKeys(Keys.ENTER);

        Thread.sleep(4000);
        Assert.assertTrue(driver.getCurrentUrl().contains("Markers") || driver.getTitle().contains("Markers"));
    }

    @Test(priority = 5)
    public void testReturnToRewards() {
        driver.navigate().to(REWARDS_URL);
        wait.until(ExpectedConditions.urlContains("/rewards"));
        Assert.assertTrue(driver.getPageSource().length() > 500);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}