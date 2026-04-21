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

public class NavigationTest extends ERBaseTest {

    @Test(priority = 1)
    public void testSaleNavigationViaUI() throws InterruptedException {
        driver.get("https://www.michaels.com/");
        handlePopups();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement saleLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href, 'sale') or contains(text(),'Sale') or contains(text(),'Ad')]")
        ));

        highlightElement(saleLink);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", saleLink);

        Thread.sleep(3000);
        wait.until(d -> d.getCurrentUrl().toLowerCase().contains("sale") || d.getCurrentUrl().toLowerCase().contains("ad"));
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("sale") ||
                driver.getCurrentUrl().toLowerCase().contains("ad"), "Sale nav failed!");
    }

    @Test(priority = 2)
    public void testSearchFunctionality() {
        driver.get("https://www.michaels.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[contains(@placeholder,'Search') or @type='search']")
        ));

        highlightElement(searchBox);
        searchBox.clear();
        searchBox.sendKeys("Yarn");
        searchBox.sendKeys(Keys.ENTER);

        wait.until(d -> d.getCurrentUrl().toLowerCase().contains("yarn"));
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("yarn"), "Search navigation failed!");
    }

    @Test(priority = 3)
    public void testBrowserBackNavigation() throws InterruptedException {
        driver.get("https://www.michaels.com/");
        String homeUrl = driver.getCurrentUrl();

        driver.get("https://www.michaels.com/shop/knitting-crochet");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(d -> !d.getCurrentUrl().equals(homeUrl));

        driver.navigate().back();
        Thread.sleep(2000);

        Assert.assertTrue(driver.getCurrentUrl().contains("michaels.com"), "Back button failed!");
    }

    @Test(priority = 4)
    public void testSocialMediaFooterLinks() {
        driver.get("https://www.michaels.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // 1. Scroll to the very bottom of the page to find the footer
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        // 2. Locate the Pinterest link (since you've worked on Pinterest automation before!)
        WebElement pinterestLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//a[contains(@href, 'pinterest.com/michaelsstores')]")
        ));

        highlightElement(pinterestLink);

        // 3. Verify the link is what we expect
        String href = pinterestLink.getAttribute("href");
        Assert.assertTrue(href.contains("pinterest.com"), "Pinterest footer link is missing or incorrect!");
        System.out.println(">>> Verified Social Link: " + href);
    }

    @Test(priority = 5)
    public void testMobileViewportResponsiveness() throws InterruptedException {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));
        driver.get("https://www.michaels.com/");
        Thread.sleep(3000);

        boolean isMainVisible = (boolean) ((JavascriptExecutor) driver).executeScript(
                "return document.body.offsetWidth > 0;"
        );

        Assert.assertTrue(isMainVisible, "Mobile view failed to render.");
        driver.manage().window().maximize();
    }

    // --- HELPERS ---

    private void highlightElement(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
            js.executeScript("arguments[0].setAttribute('style', 'background: yellow; border: 4px solid #FF00FF;');", element);
            Thread.sleep(1000);
        } catch (Exception e) { }
    }

    private void handlePopups() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));
            WebElement closeBtn = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@aria-label,'Close') or contains(@class,'close')]")));
            closeBtn.click();
            System.out.println(">>> POPUP DISMISSED");
        } catch (Exception e) {
            System.out.println(">>> NO POPUP FOUND");
        }
    }
}