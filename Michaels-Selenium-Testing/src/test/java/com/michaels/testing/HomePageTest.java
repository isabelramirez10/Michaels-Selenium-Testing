package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.Cookie;

public class HomePageTest extends BaseTest {

    // 1. Page title contains "Michaels"
    @Test
    public void testPageTitleContainsMichaels() throws InterruptedException {
        Thread.sleep(2000);
        String title = driver.getTitle();
        Thread.sleep(1500);
        Assert.assertTrue(title.toLowerCase().contains("michaels"),
                "Page title should contain 'Michaels'. Actual: " + title);
    }

    // 2. Logo or home link is visible in the header
    @Test
    public void testLogoIsDisplayed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        Thread.sleep(2000);
        WebElement logo = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("header a, .header a, nav a[href='/'], a[href='https://www.michaels.com']")));
        Thread.sleep(1500);
        Assert.assertTrue(logo.isDisplayed(),
                "A logo or home link should be visible in the header.");
    }

    // 3. Search bar accepts input
    @Test
    public void testSearchBarAcceptsInput() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='search'], input[placeholder*='Search']")));
        Thread.sleep(1500);
        searchBar.sendKeys("paint");
        Thread.sleep(1500);
        Assert.assertEquals(searchBar.getAttribute("value"), "paint",
                "Search bar should accept and retain typed input.");
    }

    // 4. Scroll down the home page and back up
    @Test
    public void testHomePageScrolling() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(1500);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(1500);
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertTrue(body.isDisplayed(),
                "Page should still be displayed after scrolling.");
    }

    // 5. Submit feedback popup (website suggestion, speed, 5 stars, no comment, no contact)
    @Test
    public void testFeedbackPopup() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        Thread.sleep(3000);

        // Look for a feedback button/tab on the page
        WebElement feedbackBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("[class*='feedback'], [id*='feedback'], button[aria-label*='feedback'], [class*='survey'], [id*='survey']")));
        Thread.sleep(1500);
        feedbackBtn.click();
        Thread.sleep(2000);

        // Select "Website Suggestion" as the feedback type
        WebElement websiteSuggestion = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(),'Website') or contains(text(),'Suggestion') or contains(text(),'suggestion')]")));
        Thread.sleep(1500);
        websiteSuggestion.click();
        Thread.sleep(1500);

        // Select "Speed" as the topic
        WebElement speedOption = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[contains(text(),'Speed') or contains(text(),'speed') or contains(text(),'Performance')]")));
        Thread.sleep(1500);
        speedOption.click();
        Thread.sleep(1500);

        // Select 5 star experience
        List<WebElement> stars = driver.findElements(
                By.cssSelector("[class*='star'], [aria-label*='star'], [class*='rating'] span, [class*='Star']"));
        if (stars.size() >= 5) {
            Thread.sleep(1000);
            stars.get(4).click(); // click the 5th star
            Thread.sleep(1500);
        }

        // Leave comment box empty and submit
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[type='submit'], [class*='submit'], [id*='submit']")));
        Thread.sleep(1500);
        submitBtn.click();
        Thread.sleep(2000);

        // Verify submission was acknowledged
        String bodyText = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                bodyText.contains("thank") || bodyText.contains("submitted") ||
                        bodyText.contains("received") || bodyText.contains("feedback"),
                "Feedback submission should show a thank you or confirmation message.");
    }
}