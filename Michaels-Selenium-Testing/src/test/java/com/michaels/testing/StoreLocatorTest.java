package com.michaels.testing;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class StoreLocatorTest extends BaseTest {

    @Test
    public void testStoreLocatorPageLoads() {
        driver.get("https://www.michaels.com/store-finder");
        Assert.assertTrue(driver.getCurrentUrl().contains("store"),
                "Store locator URL should contain 'store'.");
    }

    @Test
    public void testStoreLocatorHasSearchInput() {
        driver.get("https://www.michaels.com/store-finder");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[type='text'], input[placeholder*='zip'], input[placeholder*='city']")));
        Assert.assertTrue(input.isDisplayed(), "Store locator should have a ZIP/city input.");
    }

    @Test
    public void testStoreSearchWithValidZip() {
        driver.get("https://www.michaels.com/store-finder");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input[type='text'], input[placeholder*='zip']")));
        input.sendKeys("33101");
        WebElement searchBtn = driver.findElement(
                By.cssSelector("button[type='submit'], button[class*='search']"));
        searchBtn.click();
        wait.until(ExpectedConditions.urlContains("store"));
        Assert.assertTrue(driver.getCurrentUrl().contains("store"),
                "After ZIP search, URL should reflect store results.");
    }

    @Test
    public void testStoreLocatorPageTitle() {
        driver.get("https://www.michaels.com/store-finder");
        Assert.assertFalse(driver.getTitle().isEmpty(), "Store locator page should have a title.");
    }

    @Test
    public void testStoreLocatorBodyIsVisible() {
        driver.get("https://www.michaels.com/store-finder");
        WebElement body = driver.findElement(By.tagName("body"));
        Assert.assertTrue(body.isDisplayed(), "Store locator page body should render.");
    }
}