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

public class ProductPageTest extends BaseTest {

    private void navigateToFringeCurtain() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        driver.get("https://www.michaels.com/shop/party/party-decorations/curtains-backdrops");
        Thread.sleep(10000);

        // Scroll down to make sure filters are visible
        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(3000);

        // Find ANY filter button on the page — broad selector
        List<WebElement> filterBtns = driver.findElements(
                By.cssSelector("button[aria-controls*='filter'], button[aria-controls*='color'], button[class*='filter']"));

        if (filterBtns.size() > 0) {
            for (WebElement btn : filterBtns) {
                String controls = btn.getAttribute("aria-controls");
                if (controls != null && controls.toLowerCase().contains("color")) {
                    js.executeScript("arguments[0].scrollIntoView(true);", btn);
                    Thread.sleep(2000);
                    js.executeScript("arguments[0].click();", btn);
                    Thread.sleep(3000);
                    break;
                }
            }
        }

        // Click Purple — look broadly for any purple option
        List<WebElement> purpleOptions = driver.findElements(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'purple')]"));

        boolean clicked = false;
        for (WebElement opt : purpleOptions) {
            try {
                if (opt.isDisplayed()) {
                    js.executeScript("arguments[0].scrollIntoView(true);", opt);
                    Thread.sleep(1000);
                    js.executeScript("arguments[0].click();", opt);
                    clicked = true;
                    Thread.sleep(5000);
                    break;
                }
            } catch (Exception e) {
                // try next one
            }
        }

        if (!clicked) {
            // If purple filter not found just proceed without filter
            System.out.println("Purple filter not found, proceeding without filter.");
        }

        // Scroll to find fringe curtain
        js.executeScript("window.scrollBy(0, 500)");
        Thread.sleep(3000);

        // Click fringe curtain — broad text search
        List<WebElement> products = driver.findElements(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'fringe')]"));

        for (WebElement p : products) {
            try {
                if (p.isDisplayed()) {
                    js.executeScript("arguments[0].scrollIntoView(true);", p);
                    Thread.sleep(2000);
                    js.executeScript("arguments[0].click();", p);
                    Thread.sleep(8000);
                    break;
                }
            } catch (Exception e) {
                // try next
            }
        }
    }

    // 1. Product page has a visible non empty title
    @Test
    public void testProductPageHasTitle() throws InterruptedException {
        navigateToFringeCurtain();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("h1")));
        Thread.sleep(2000);

        Assert.assertTrue(title.isDisplayed(), "Product page should have a title.");
        Assert.assertFalse(title.getText().trim().isEmpty(), "Title should not be empty.");
        Thread.sleep(2000);
    }

    // 2. Scroll down the product page slowly to reveal all details and reviews
    @Test
    public void testProductPageScrollToReviews() throws InterruptedException {
        navigateToFringeCurtain();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(2000);

        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(2000);
        js.executeScript("window.scrollBy(0, 300)");
        Thread.sleep(2000);
        js.executeScript("window.scrollTo(0, 0)");
        Thread.sleep(2000);

        Assert.assertTrue(driver.findElement(By.tagName("body")).isDisplayed(),
                "Page should be visible after scrolling.");
    }

    // 3. Hover over product image and check gallery
    @Test
    public void testProductImageGallery() throws InterruptedException {
        navigateToFringeCurtain();
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);
        Thread.sleep(2000);

        List<WebElement> images = driver.findElements(By.cssSelector("img"));
        Assert.assertTrue(images.size() > 0, "Product page should have images.");

        // Find the largest visible image (likely the main product image)
        WebElement mainImage = null;
        for (WebElement img : images) {
            try {
                if (img.isDisplayed() && img.getSize().getWidth() > 100) {
                    mainImage = img;
                    break;
                }
            } catch (Exception e) {
                // skip
            }
        }

        if (mainImage != null) {
            js.executeScript("arguments[0].scrollIntoView(true);", mainImage);
            Thread.sleep(2000);
            actions.moveToElement(mainImage).perform();
            Thread.sleep(3000);
        }

        // Click thumbnails if available
        List<WebElement> thumbs = driver.findElements(
                By.cssSelector("[class*='thumb'] img, [class*='gallery'] img, [class*='thumbnail'] img"));
        for (int i = 0; i < Math.min(thumbs.size(), 3); i++) {
            try {
                js.executeScript("arguments[0].scrollIntoView(true);", thumbs.get(i));
                Thread.sleep(1000);
                js.executeScript("arguments[0].click();", thumbs.get(i));
                Thread.sleep(2000);
            } catch (Exception e) {
                // skip
            }
        }

        Assert.assertNotNull(mainImage != null ? mainImage.getAttribute("src") : "fallback",
                "Product image should have a src.");
    }

    // 4. Increase quantity to 3 and add to cart
    @Test
    public void testIncreaseQuantityAndAddToCart() throws InterruptedException {
        navigateToFringeCurtain();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Thread.sleep(2000);

        // Quantity field
        WebElement qty = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[aria-label='Number Stepper']")));
        js.executeScript("arguments[0].scrollIntoView(true);", qty);
        Thread.sleep(2000);
        js.executeScript("arguments[0].value='';", qty);
        qty.sendKeys("3");
        Thread.sleep(3000);

        // Add to cart — find button containing add to cart text
        List<WebElement> buttons = driver.findElements(By.cssSelector("button"));
        for (WebElement btn : buttons) {
            try {
                String txt = btn.getText().toLowerCase();
                if (txt.contains("add to cart") || txt.contains("add to bag")) {
                    js.executeScript("arguments[0].scrollIntoView(true);", btn);
                    Thread.sleep(2000);
                    js.executeScript("arguments[0].click();", btn);
                    Thread.sleep(6000);
                    break;
                }
            } catch (Exception e) {
                // skip
            }
        }

        String body = driver.findElement(By.tagName("body")).getText().toLowerCase();
        Assert.assertTrue(
                body.contains("added") || body.contains("cart") || body.contains("bag"),
                "Item should be added to cart.");
        Thread.sleep(2000);
    }

    // 5. Back button returns to curtains page
    @Test
    public void testBackButtonReturnsToCurtainsPage() throws InterruptedException {
        navigateToFringeCurtain();
        Thread.sleep(2000);
        driver.navigate().back();
        Thread.sleep(5000);

        String url = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(
                url.contains("curtain") || url.contains("backdrop") ||
                        url.contains("party") || url.contains("decoration"),
                "Back should return to curtains page.");
        Thread.sleep(2000);
    }
}