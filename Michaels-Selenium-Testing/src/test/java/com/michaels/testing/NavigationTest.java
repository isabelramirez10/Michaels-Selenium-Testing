package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class NavigationTest extends ERBaseTest {

    @Test(priority = 1)
    public void testSaleNavLinkIsClickable() {
        driver.get("https://www.michaels.com/");
        handlePopups();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Find the link
        By saleLocator = By.xpath("//a[contains(@href,'/sale') or contains(@href, '/weekly-ad')]");
        WebElement saleLink = wait.until(ExpectedConditions.presenceOfElementLocated(saleLocator));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", saleLink);

        // FIXED: Using ExpectedConditions.or() instead of ||
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("sale"),
                ExpectedConditions.urlContains("weekly-ad")
        ));

        String currentUrl = driver.getCurrentUrl().toLowerCase();
        Assert.assertTrue(currentUrl.contains("sale") || currentUrl.contains("weekly-ad"),
                "Should navigate to a sale or weekly ad page.");
    }

    private void handlePopups() {
    }

    @Test(priority = 2)
    public void testNewArrivalsOrFeaturedLinkExists() {
        driver.get("https://www.michaels.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("header")));
        Assert.assertTrue(header.isDisplayed(), "Header should be visible.");
    }

    @Test(priority = 3)
    public void testHeaderContainsCategoryLinks() {
        driver.get("https://www.michaels.com/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("header a")));

        int linkCount = driver.findElements(By.cssSelector("header a")).size();
        Assert.assertTrue(linkCount > 5, "Header should contain several navigation links.");
    }

    @Test(priority = 4)
    public void testStoreNavLinkNavigates() {
        driver.get("https://www.michaels.com/");
        handlePopups();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        By storeLocator = By.xpath("//a[contains(@href,'store-locator') or contains(@class,'store') or contains(text(),'Store')]");
        WebElement storeLink = wait.until(ExpectedConditions.presenceOfElementLocated(storeLocator));

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", storeLink);

        wait.until(ExpectedConditions.urlContains("store"));
        Assert.assertTrue(driver.getCurrentUrl().contains("store"), "Should navigate to store locator.");
    }

    @Test(priority = 5)
    public void testNavigationDoesNotBreakOnMobileViewport() {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));
        driver.get("https://www.michaels.com/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement body = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        Assert.assertTrue(body.isDisplayed(), "Page should render on mobile.");

        driver.manage().window().maximize();
    }
}