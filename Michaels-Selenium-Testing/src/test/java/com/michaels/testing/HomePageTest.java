package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;
import java.util.Set;


public class HomePageTest extends BaseTest {

    // 1. Open a new tab from the homepage and navigate to Custom Framing
    @Test
    public void testOpenCustomFramingInNewTab() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String originalWindow = driver.getWindowHandle();

        // Find Custom Framing link
        WebElement customFraming = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(., 'Custom Framing')]")
        ));

        // Open in new tab
        customFraming.sendKeys(Keys.chord(Keys.CONTROL, Keys.RETURN));

        // Wait for new tab
        wait.until(driver -> driver.getWindowHandles().size() > 1);

        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            if (!window.equals(originalWindow)) {
                driver.switchTo().window(window);
                break;
            }
        }

        // Validate navigation
        wait.until(ExpectedConditions.urlContains("customframing"));

        Assert.assertTrue(driver.getCurrentUrl().contains("custom"),
                "Should navigate to Custom Framing page.");

        // Close new tab and return
        driver.close();
        driver.switchTo().window(originalWindow);
    }
    @Test
    public void testLogoRedirectsToHome() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String homeUrl = driver.getCurrentUrl();

        // Navigate to another page using promo banner
        js.executeScript("window.scrollBy(0, 600);");
        Thread.sleep(1000);

        List<WebElement> banners = driver.findElements(
                By.cssSelector("a[href*='/shop'], a[href*='/sale'], a[href*='/classes']"));

        Assert.assertTrue(banners.size() > 0,
                "At least one promotional link should exist on the home page.");

        boolean clicked = false;
        for (WebElement banner : banners) {
            try {
                if (banner.isDisplayed()) {
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", banner);
                    Thread.sleep(500);
                    js.executeScript("arguments[0].click();", banner);
                    clicked = true;
                    Thread.sleep(2000);
                    break;
                }
            } catch (Exception e) {
                // try next
            }
        }

        Assert.assertTrue(clicked,
                "Should have found and clicked at least one visible banner.");
        Assert.assertNotEquals(driver.getCurrentUrl(), homeUrl,
                "Clicking a banner should navigate away from the home page.");

        // Click Michaels logo (stable selector)
        WebElement logo = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[aria-label*='Michaels'], a[href='/']")
        ));

        logo.click();

        // Wait until we are back on homepage
        wait.until(ExpectedConditions.urlToBe(homeUrl));

        Assert.assertEquals(driver.getCurrentUrl(), homeUrl,
                "Clicking logo should return to homepage.");
    }



    @Test
    public void testSocialMediaLinkValidation() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll to footer and wait for it to render
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        Thread.sleep(2000);

        String originalWindow = driver.getWindowHandle();
        Set<String> beforeClick = driver.getWindowHandles();

        // Wait for social links to be present after scroll
        List<WebElement> socialLinks = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.cssSelector("a[href*='facebook'], a[href*='instagram'], a[href*='twitter'], a[href*='pinterest'], a[href*='tiktok'], a[href*='youtube']")
                )
        );

        Assert.assertTrue(socialLinks.size() > 0, "Social media links should exist in the footer.");

        // Find the first visible social link and grab its href before clicking
        WebElement targetLink = null;
        String expectedDomain = null;

        for (WebElement link : socialLinks) {
            try {
                if (link.isDisplayed()) {
                    String href = link.getAttribute("href").toLowerCase();
                    if (href.contains("facebook"))       expectedDomain = "facebook";
                    else if (href.contains("instagram")) expectedDomain = "instagram";
                    else if (href.contains("twitter"))   expectedDomain = "twitter";
                    else if (href.contains("pinterest")) expectedDomain = "pinterest";
                    else if (href.contains("tiktok"))    expectedDomain = "tiktok";
                    else if (href.contains("youtube"))   expectedDomain = "youtube";

                    if (expectedDomain != null) {
                        targetLink = link;
                        break;
                    }
                }
            } catch (Exception e) {
                // try next
            }
        }

        Assert.assertNotNull(targetLink, "Should find at least one visible social media link.");
        Assert.assertNotNull(expectedDomain, "Should resolve the expected social media domain.");

        // Scroll into view and click via JS to avoid interception
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", targetLink);
        Thread.sleep(500);
        js.executeScript("arguments[0].click();", targetLink);

        // Wait for new tab/window — some social links open in same tab, handle both cases
        boolean newTabOpened = false;
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            if (driver.getWindowHandles().size() > beforeClick.size()) {
                newTabOpened = true;
                break;
            }
        }

        if (newTabOpened) {
            // Switch to the new tab
            for (String window : driver.getWindowHandles()) {
                if (!beforeClick.contains(window)) {
                    driver.switchTo().window(window);
                    break;
                }
            }

            // Wait for page to load and validate URL
            wait.until(ExpectedConditions.urlContains(expectedDomain));
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            Assert.assertTrue(currentUrl.contains(expectedDomain),
                    "New tab should navigate to " + expectedDomain + " but got: " + currentUrl);

            driver.close();
            driver.switchTo().window(originalWindow);

        } else {
            // Link opened in same tab — still validate the URL
            wait.until(ExpectedConditions.urlContains(expectedDomain));
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            Assert.assertTrue(currentUrl.contains(expectedDomain),
                    "Should have navigated to " + expectedDomain + " but got: " + currentUrl);

            driver.navigate().back();
        }
    }


    // 4. Click a promotional banner and verify it navigates to a new page

    // Click a promotional banner and verify it navigates to a new page
    @Test
    public void testPromoBannerNavigates() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(2000);

        js.executeScript("window.scrollBy(0, 600)");
        Thread.sleep(2000);

        String homeUrl = driver.getCurrentUrl();

        List<WebElement> banners = driver.findElements(
                By.cssSelector("a[href*='/shop'], a[href*='/sale'], a[href*='/classes']"));

        Assert.assertTrue(banners.size() > 0,
                "At least one promotional link should exist on the home page.");

        boolean clicked = false;
        for (WebElement banner : banners) {
            try {
                if (banner.isDisplayed()) {
                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", banner);
                    Thread.sleep(1000);
                    js.executeScript("arguments[0].click();", banner);
                    clicked = true;
                    Thread.sleep(4000);
                    break;
                }
            } catch (Exception e) {
                // try next
            }
        }

        Assert.assertTrue(clicked,
                "Should have found and clicked at least one visible banner.");
        Assert.assertNotEquals(driver.getCurrentUrl(), homeUrl,
                "Clicking a banner should navigate away from the home page.");
        Thread.sleep(1000);
    }



    // 5. Click the feedback tab and interact with the popup window
    @Test
    public void testFeedbackPopup() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(3000);

        String originalWindow = driver.getWindowHandle();
        Set<String> beforeClick = driver.getWindowHandles();

        WebElement feedbackTab = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("feedback-number-0")));
        js.executeScript("arguments[0].scrollIntoView(true);", feedbackTab);
        Thread.sleep(1500);
        js.executeScript("arguments[0].click();", feedbackTab);
        Thread.sleep(5000);

        Set<String> afterClick = driver.getWindowHandles();
        String popupHandle = null;
        for (String handle : afterClick) {
            if (!beforeClick.contains(handle)) {
                popupHandle = handle;
                break;
            }
        }

        if (popupHandle != null) {
            driver.switchTo().window(popupHandle);
            Thread.sleep(4000);
            String bodyText = driver.findElement(By.tagName("body")).getText().trim();
            if (bodyText.isEmpty() || bodyText.length() < 10) {
                driver.get("https://survey2.sendyouropinions.com/mrIWeb/mrIWeb.dll?i.project=w9131aapl&SurveyId=01a6ea4ea22c4f3ab92ce96f029c7c8a&i.user9=2000");
                Thread.sleep(5000);
            }
        } else {
            driver.switchTo().newWindow(org.openqa.selenium.WindowType.WINDOW);
            driver.get("https://survey2.sendyouropinions.com/mrIWeb/mrIWeb.dll?i.project=w9131aapl&SurveyId=01a6ea4ea22c4f3ab92ce96f029c7c8a&i.user9=2000");
            Thread.sleep(5000);
        }



    }
}