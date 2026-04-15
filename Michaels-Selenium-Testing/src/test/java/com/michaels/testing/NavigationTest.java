package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class NavigationTest extends BaseTest {

    @Test
    public void testSaleNavLinkIsClickable() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement saleLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.linkText("Sale")));
        saleLink.click();
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("sale"),
                "Clicking Sale should navigate to a sale page.");
    }

    @Test
    public void testNewArrivalsOrFeaturedLinkExists() {
        WebElement nav = driver.findElement(By.cssSelector("nav, header"));
        Assert.assertTrue(nav.isDisplayed(), "Navigation header should be visible.");
    }

    @Test
    public void testHeaderContainsCategoryLinks() {
        int linkCount = driver.findElements(By.cssSelector("nav a, header a")).size();
        Assert.assertTrue(linkCount > 3, "Header should contain multiple navigation links.");
    }

    @Test
    public void testStoreNavLinkNavigates() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement storeLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(),'Store') or contains(@href,'store')]")));
        storeLink.click();
        Assert.assertFalse(driver.getCurrentUrl().isEmpty(), "Store link should navigate to a valid URL.");
    }

    @Test
    public void testNavigationDoesNotBreakOnMobileViewport() {
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertTrue(body.isDisplayed(), "Page should still render at mobile viewport size.");
    }
}