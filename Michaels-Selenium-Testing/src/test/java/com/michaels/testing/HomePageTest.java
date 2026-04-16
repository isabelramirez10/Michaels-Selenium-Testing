package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;
import java.util.Set;

public class HomePageTest extends BaseTest {

    // 1. Hover over each main navigation category one by one
    @Test
    public void testHoverOverNavigationCategories() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Actions actions = new Actions(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(2000);

        List<WebElement> navItems = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("p.chakra-text.css-w54l2g")));

        Assert.assertTrue(navItems.size() > 0, "Navigation items should be present.");

        for (WebElement item : navItems) {
            try {
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", item);
                Thread.sleep(500);
                actions.moveToElement(item).perform();
                Thread.sleep(1500);
            } catch (Exception e) {
                // skip
            }
        }

        actions.moveToElement(driver.findElement(
                By.cssSelector("input[aria-label='Search Input']"))).perform();
        Thread.sleep(1000);

        Assert.assertTrue(driver.getCurrentUrl().contains("michaels.com"),
                "Page should still be on Michaels after hovering navigation.");
    }

    // 2. Type slowly in the search bar and wait for autocomplete
    @Test
    public void testSearchBarAutocomplete() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        Thread.sleep(2000);

        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[aria-label='Search Input']")));
        searchBar.click();
        Thread.sleep(1000);

        String keyword = "acr";
        for (char c : keyword.toCharArray()) {
            searchBar.sendKeys(String.valueOf(c));
            Thread.sleep(400);
        }

        Thread.sleep(3000);

        Assert.assertEquals(searchBar.getAttribute("value"), "acr",
                "Search bar should contain the typed text.");
    }

    // 3. Scroll down the entire home page slowly stopping at each section
    @Test
    public void testHomePageScrolling() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(2000);

        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 400)");
        Thread.sleep(2000);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(2000);

        Assert.assertTrue(driver.findElement(By.tagName("body")).isDisplayed(),
                "Page should still be displayed after scrolling.");
    }

    // 4. Click a promotional banner and verify it navigates to a new page
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
/*
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

        // Step 1 — Select Website Suggestion
        try {
            WebElement websiteOpt = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Website') or contains(text(),'Suggestion') or contains(text(),'website')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", websiteOpt);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Website suggestion not found: " + e.getMessage());
        }

        // Step 2 — Continue after Website Suggestion
        try {
            WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Continue')] | //input[@value='Continue'] | //a[contains(text(),'Continue')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", continueBtn);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Continue after website suggestion not found: " + e.getMessage());
        }

        // Step 3 — Select Site Speed
        try {
            WebElement speedOpt = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'Site Speed') or contains(text(),'Speed') or contains(text(),'speed') or contains(text(),'Performance')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", speedOpt);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Site Speed not found: " + e.getMessage());
        }

        // Step 4 — Continue after Site Speed
        try {
            WebElement continueBtn2 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Continue')] | //input[@value='Continue'] | //a[contains(text(),'Continue')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", continueBtn2);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Continue after site speed not found: " + e.getMessage());
        }

        // Step 5 — Select 5 stars
        try {
            List<WebElement> stars = driver.findElements(
                    By.cssSelector("input[type='radio'], [class*='star'], [class*='Star'], label[for*='5']"));
            if (stars.size() >= 5) {
                Thread.sleep(1000);
                js.executeScript("arguments[0].click();", stars.get(4));
                Thread.sleep(2000);
            } else if (stars.size() > 0) {
                Thread.sleep(1000);
                js.executeScript("arguments[0].click();", stars.get(stars.size() - 1));
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("Stars not found: " + e.getMessage());
        }

        // Step 6 — Continue after stars
        try {
            WebElement continueBtn3 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Continue')] | //input[@value='Continue'] | //a[contains(text(),'Continue')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", continueBtn3);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Continue after stars not found: " + e.getMessage());
        }

        // Step 7 — Select No Comment
        try {
            WebElement noComment = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'No Comment') or contains(text(),'no comment') or contains(text(),'No comment') or contains(text(),'Skip') or contains(text(),'skip')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", noComment);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("No comment not found: " + e.getMessage());
        }

        // Step 8 — Continue after No Comment
        try {
            WebElement continueBtn4 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Continue')] | //input[@value='Continue'] | //a[contains(text(),'Continue')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", continueBtn4);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Continue after no comment not found: " + e.getMessage());
        }

        // Step 9 — Select No to contact
        try {
            WebElement noContact = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[contains(text(),'No') and not(contains(text(),'Note')) and not(contains(text(),'None'))] | //input[@value='No'] | //label[contains(text(),'No')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", noContact);
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("No to contact not found: " + e.getMessage());
        }

        // Step 10 — Continue after No to contact
        try {
            WebElement continueBtn5 = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Continue')] | //input[@value='Continue'] | //a[contains(text(),'Continue')]")));
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", continueBtn5);
            Thread.sleep(3000);
        } catch (Exception e) {
            System.out.println("Continue after no contact not found: " + e.getMessage());
        }

        // Verify confirmation
        String finalBody = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                finalBody.contains("thank") || finalBody.contains("submitted") ||
                        finalBody.contains("complete") || finalBody.contains("received") ||
                        finalBody.contains("feedback") || finalBody.contains("response"),
                "Survey should show a confirmation after submission.");
        Thread.sleep(2000);

        // Close popup and return to Michaels
        driver.close();
        Thread.sleep(1000);
        driver.switchTo().window(originalWindow);
        Thread.sleep(1000);
    }*/
}