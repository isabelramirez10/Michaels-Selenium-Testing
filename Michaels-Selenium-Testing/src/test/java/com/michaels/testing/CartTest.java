package com.michaels.testing;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.util.List;

public class CartTest {

    private WebDriver          driver;
    private WebDriverWait      wait;
    private JavascriptExecutor js;

    private static final String BASE_URL = "https://www.michaels.com";

    private static final By SEARCH_INPUT = By.cssSelector(
            "input[type='search'], " +
                    "input[placeholder*='Search'], " +
                    "input[aria-label*='Search'], " +
                    "input[name='q']");

    private static final By PRODUCT_LINK = By.cssSelector("a#productName");

    private static final By ADD_TO_CART_BTN = By.cssSelector("button#add-to-cart");

    private static final By CART_ITEM = By.cssSelector("p.productName");

    private static final By QTY_INCREASE_BTN = By.cssSelector(
            "div[role='button'][aria-label*='increment'], " +
                    "div[role='button'][aria-label*='ncrease']");

    private static final By QTY_INPUT = By.cssSelector(
            "input[aria-label*='number stepper'], " +
                    "input[class*='number-stepper'], " +
                    "input[data-testid*='quantity']");

    private static final By REMOVE_BTN = By.cssSelector(
            "button[aria-label*='emove'], " +
                    "button[data-testid*='remove'], " +
                    "[class*='remove-btn'], " +
                    "[class*='RemoveItem'], " +
                    "[class*='delete-item'], " +
                    "a[class*='remove']");

    @BeforeClass
    public void setUpClass() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/124.0.0.0 Safari/537.36");

        driver = new ChromeDriver(options);
        js     = (JavascriptExecutor) driver;
        wait   = new WebDriverWait(driver, Duration.ofSeconds(25));

        js.executeScript(
                "Object.defineProperty(navigator,'webdriver',{get:()=>undefined})");

        driver.get(BASE_URL);
        dismissOverlays();
    }

    @AfterClass
    public void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void dismissOverlays() {
        try {
            js.executeScript(
                    "document.querySelectorAll(" +
                            "'.modal-backdrop,.osano-cm-window," +
                            "#onetrust-banner-sdk,[class*=\"overlay\"]')" +
                            ".forEach(el => el.style.display='none');");
        } catch (Exception ignored) {}

        try {
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(By.cssSelector(
                            "button[aria-label='Close']," +
                                    "button[aria-label='close']," +
                                    "[class*='modal'] button[class*='close']," +
                                    "[class*='popup'] button[class*='close']," +
                                    "button[data-testid='close-button']")))
                    .click();
            System.out.println("[dismissOverlays] Popup closed.");
        } catch (Exception ignored) {}
    }

    private void scrollAndClick(WebElement el) throws InterruptedException {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        Thread.sleep(400);
        js.executeScript("arguments[0].click();", el);
    }

    private void humanType(WebElement el, String text) throws InterruptedException {
        el.click();
        Thread.sleep(300);
        el.clear();
        for (char c : text.toCharArray()) {
            el.sendKeys(String.valueOf(c));
            Thread.sleep(75);
        }
    }

    private void goToCart() throws InterruptedException {
        driver.get(BASE_URL + "/cart");
        wait.until(ExpectedConditions.urlContains("/cart"));
        Thread.sleep(1500);
        dismissOverlays();
    }

    private String capturePrice() {
        By priceLocator = By.cssSelector(
                ".cart-item-price," +
                        "[data-testid='cart-item-price']," +
                        ".line-price," +
                        "[class*='item-price']," +
                        "[class*='ItemPrice']," +
                        ".subtotal");
        try {
            return driver.findElement(priceLocator).getText().trim();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    @Test(description = "Search for the fringe curtain, click it from results, add it to the cart")
    public void test1_AddItemToCart() throws InterruptedException {

        // Step 1: type the product name into the search bar
        WebElement searchBox = wait.until(
                ExpectedConditions.elementToBeClickable(SEARCH_INPUT));
        humanType(searchBox, "8ft x 3ft Fringe Curtain by Celebrate It");
        searchBox.sendKeys(Keys.RETURN);
        System.out.println("[test1] Search submitted.");

        // Step 2: wait for results and click the first product tile
        WebElement firstProduct = wait.until(
                ExpectedConditions.elementToBeClickable(PRODUCT_LINK));
        String productName = firstProduct.getText().trim();
        System.out.println("[test1] Clicking product: " + productName);
        scrollAndClick(firstProduct);

        // Step 3: wait for PDP to load then click Add to Cart
        WebElement addBtn = wait.until(
                ExpectedConditions.elementToBeClickable(ADD_TO_CART_BTN));
        dismissOverlays();
        Thread.sleep(600);
        scrollAndClick(addBtn);
        System.out.println("[test1] Add to Cart clicked.");

        // Step 4: click the View Cart button that pops up
        By viewCartLocator = By.xpath(
                "//button[.//div[text()='View Cart']] | " +
                        "//button[normalize-space()='View Cart'] | " +
                        "//a[normalize-space()='View Cart']");

        try {
            WebElement viewCartBtn = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.elementToBeClickable(viewCartLocator));
            scrollAndClick(viewCartBtn);
            System.out.println("[test1] View Cart clicked.");
            wait.until(ExpectedConditions.urlContains("/cart"));
            Thread.sleep(1500);
        } catch (TimeoutException e) {
            System.out.println("[test1] No View Cart button — navigating directly.");
            goToCart();
        }

        // Step 5: assert item is in the cart
        List<WebElement> items = driver.findElements(CART_ITEM);
        Assert.assertFalse(items.isEmpty(),
                "Cart should contain at least one item after adding a product");

        System.out.println("[test1_AddItemToCart] PASS — cart items: " + items.size());
    }

    @Test(
            description      = "Increase the cart item quantity to 3 and verify the price updates",
            dependsOnMethods = "test1_AddItemToCart"
    )
    public void test2_IncreaseQuantityToThree() throws InterruptedException {

        if (!driver.getCurrentUrl().contains("/cart")) {
            goToCart();
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(CART_ITEM));

        String priceBefore = capturePrice();
        System.out.println("[test2] Price before: " + priceBefore);

        boolean quantitySet = false;

        // Strategy A: direct input
        try {
            WebElement qtyInput = new WebDriverWait(driver, Duration.ofSeconds(8))
                    .until(ExpectedConditions.visibilityOfElementLocated(QTY_INPUT));
            scrollAndClick(qtyInput);
            Thread.sleep(300);

            js.executeScript(
                    "var setter = Object.getOwnPropertyDescriptor(" +
                            "window.HTMLInputElement.prototype,'value').set;" +
                            "setter.call(arguments[0],'3');" +
                            "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                            "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                    qtyInput);

            qtyInput.sendKeys(Keys.TAB);
            quantitySet = true;
            System.out.println("[test2] Set via direct input.");

        } catch (TimeoutException e) {
            System.out.println("[test2] No input found — trying stepper button.");
        }

        // Strategy B: click the increment div twice (1 -> 2 -> 3)
        if (!quantitySet) {
            try {
                for (int i = 0; i < 2; i++) {
                    WebElement btn = wait.until(
                            ExpectedConditions.elementToBeClickable(QTY_INCREASE_BTN));
                    scrollAndClick(btn);
                    Thread.sleep(1000);
                }
                quantitySet = true;
                System.out.println("[test2] Set via stepper (x2).");
            } catch (TimeoutException e) {
                System.out.println("[test2] Stepper also not found.");
            }
        }

        Assert.assertTrue(quantitySet,
                "A quantity input or stepper button must be interactable on the cart page");

        Thread.sleep(2500);
        String priceAfter = capturePrice();
        System.out.println("[test2] Price after: " + priceAfter);

        Assert.assertNotEquals(priceAfter, priceBefore,
                "Line-item price should change after increasing quantity to 3");

        System.out.println("[test2_IncreaseQuantityToThree] PASS");
    }

    @Test(
            description      = "Click 'Add Promo Code', enter 'MAKERSAVE', click Apply, verify UI responds",
            dependsOnMethods = "test2_IncreaseQuantityToThree"
    )
    public void test3_ApplyPromoCode() throws InterruptedException {

        if (!driver.getCurrentUrl().contains("/cart")) {
            goToCart();
        }

        // Step 1: click the "Add Promo Code" button (confirmed: button.css-1c7c9ch)
        WebElement promoToggle = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button.css-1c7c9ch")));
        scrollAndClick(promoToggle);
        Thread.sleep(800);
        System.out.println("[test3] 'Add Promo Code' clicked.");

        // Step 2: type into the input (confirmed: input#promoCode)
        WebElement promoInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("promoCode")));
        scrollAndClick(promoInput);
        Thread.sleep(300);
        promoInput.clear();
        promoInput.sendKeys("MAKERSAVE");
        System.out.println("[test3] Promo code entered: MAKERSAVE");

        // Step 3: click Apply (confirmed: button containing <div>Apply</div>)
        WebElement applyBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(
                        "//button[.//div[normalize-space()='Apply']]")));
        scrollAndClick(applyBtn);
        System.out.println("[test3] Apply clicked.");

        // Step 4: any visible feedback confirms the UI responded
        By feedbackCss = By.cssSelector(
                "[data-testid*='coupon-message'],[data-testid*='promo-message']," +
                        ".coupon-message,.promo-message," +
                        "[class*='coupon-error'],[class*='promo-error']," +
                        "[class*='coupon-success'],[class*='promo-success']," +
                        "[class*='PromoMessage'],[class*='CouponMessage']," +
                        ".error-feedback,[role='alert'],[class*='discount-applied']");

        By feedbackXpath = By.xpath(
                "//*[contains(normalize-space(),'MAKERSAVE')" +
                        " or contains(normalize-space(),'promo')" +
                        " or contains(normalize-space(),'coupon')" +
                        " or contains(normalize-space(),'discount')" +
                        " or contains(normalize-space(),'invalid')" +
                        " or contains(normalize-space(),'not valid')" +
                        " or contains(normalize-space(),'savings')" +
                        " or contains(normalize-space(),'applied')]");

        WebElement feedback;
        try {
            feedback = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackCss));
        } catch (TimeoutException e) {
            feedback = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackXpath));
        }

        Assert.assertTrue(feedback.isDisplayed(),
                "Promo code submission should produce visible feedback (success or error)");

        System.out.println("[test3_ApplyPromoCode] PASS — feedback: " + feedback.getText().trim());
    }

    @Test(
            description      = "Remove the cart item and verify the empty-cart message appears",
            dependsOnMethods = "test3_ApplyPromoCode"
    )
    public void test4_RemoveItem() throws InterruptedException {

        if (!driver.getCurrentUrl().contains("/cart")) {
            goToCart();
        }

        WebElement cartItem = wait.until(
                ExpectedConditions.visibilityOfElementLocated(CART_ITEM));
        Assert.assertTrue(cartItem.isDisplayed(),
                "A cart item must be present before removal");

        WebElement removeBtn = wait.until(
                ExpectedConditions.elementToBeClickable(REMOVE_BTN));
        scrollAndClick(removeBtn);
        System.out.println("[test4] Remove clicked.");

        wait.until(ExpectedConditions.stalenessOf(cartItem));
        Thread.sleep(1500);

        By emptyXpath = By.xpath(
                "//*[contains(" +
                        "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," +
                        "'cart is empty') or contains(" +
                        "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," +
                        "'bag is empty') or contains(" +
                        "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," +
                        "'no items in your cart') or contains(" +
                        "translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')," +
                        "'your cart is currently empty')]");

        By emptyCss = By.cssSelector(
                "[data-testid='empty-cart'],.empty-cart," +
                        "[class*='empty-cart'],[class*='cart-empty']," +
                        "[class*='EmptyCart'],.empty-bag");

        boolean emptyConfirmed = false;
        try {
            emptyConfirmed = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(emptyXpath)).isDisplayed();
        } catch (TimeoutException e) {
            try {
                emptyConfirmed = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(emptyCss)).isDisplayed();
            } catch (TimeoutException e2) {
                String src = driver.getPageSource().toLowerCase();
                emptyConfirmed = src.contains("cart is empty") ||
                        src.contains("bag is empty")               ||
                        src.contains("no items");
                System.out.println("[test4] Fallback page-source check: " + emptyConfirmed);
            }
        }

        Assert.assertTrue(emptyConfirmed,
                "An empty-cart message should appear after removing the only item");

        System.out.println("[test4_RemoveItem] PASS");
    }

    @Test(
            description      = "Click 'Continue Shopping' on the empty cart and leave /cart",
            dependsOnMethods = "test4_RemoveItem"
    )
    public void test5_ContinueShopping() throws InterruptedException {

        if (!driver.getCurrentUrl().contains("/cart")) {
            goToCart();
        }

        By continueShoppingCss = By.cssSelector(
                "[data-testid*='continue-shopping']," +
                        "[class*='continue-shopping']," +
                        "[class*='ContinueShopping']," +
                        "[class*='continue-btn']," +
                        "a[href='/']," +
                        "a[href*='shop']");

        By continueShoppingXpath = By.xpath(
                "//a[contains(normalize-space(),'Continue Shopping')" +
                        " or contains(normalize-space(),'Continue shopping')" +
                        " or contains(normalize-space(),'Keep Shopping')" +
                        " or contains(normalize-space(),'Start Shopping')]" +
                        "|//button[contains(normalize-space(),'Continue Shopping')" +
                        " or contains(normalize-space(),'Keep Shopping')" +
                        " or contains(normalize-space(),'Start Shopping')]");

        WebElement continueBtn;
        try {
            continueBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(continueShoppingCss));
        } catch (TimeoutException e) {
            continueBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(continueShoppingXpath));
        }

        System.out.println("[test5] Button text: '" + continueBtn.getText().trim() + "'");
        scrollAndClick(continueBtn);

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/cart")));

        String landedUrl = driver.getCurrentUrl();
        Assert.assertFalse(landedUrl.contains("/cart"),
                "After Continue Shopping we should leave /cart. Landed: " + landedUrl);

        System.out.println("[test5_ContinueShopping] PASS — landed: " + landedUrl);
    }
}