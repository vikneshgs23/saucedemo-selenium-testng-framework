package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

public class InventoryPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // Locators
    private final By headerTitle = By.className("title");
    private final By itemCards = By.className("inventory_item");
    private final By addBackpackButton = By.id("add-to-cart-sauce-labs-backpack");
    private final By removeBackpackButton = By.id("remove-sauce-labs-backpack");
    private final By cartBadge = By.className("shopping_cart_badge");
    private final By cartLink = By.className("shopping_cart_link");
    private final By sortDropdown = By.className("product_sort_container");
    private final By itemPrices = By.className("inventory_item_price");
    private final By itemTitles = By.className("inventory_item_name");
    private final By itemImages = By.cssSelector(".inventory_item_img img");
    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public String getPageTitle() {
        return waitUtils.waitForVisibility(headerTitle).getText().trim();
    }

    public int getItemCount() {
        waitUtils.waitForVisibility(itemCards);
        return driver.findElements(itemCards).size();
    }

    // Dynamic helper to add any item by name (e.g. "Sauce Labs Backpack" -> "add-to-cart-sauce-labs-backpack")
    public InventoryPage addItemToCart(String itemName) {
        By buttonLocator = By.id("add-to-cart-" + itemName.toLowerCase().replace(" ", "-"));
        waitUtils.waitForClickable(buttonLocator).click();
        return this;
    }

    public InventoryPage addBackpackToCart() {
        waitUtils.waitForClickable(addBackpackButton).click();
        return this;
    }

    public InventoryPage removeBackpackFromCart() {
        waitUtils.waitForClickable(removeBackpackButton).click();
        return this;
    }

    public String getCartBadgeCount() {
        return waitUtils.waitForVisibility(cartBadge).getText().trim();
    }

    public boolean isCartBadgeDisplayed() {
        try {
            return driver.findElement(cartBadge).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    // Transitions directly to CartPage
    public CartPage clickCart() {
        WebElement cartElement = waitUtils.waitForClickable(cartLink);
        try {
            cartElement.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", cartElement);
        }
        waitUtils.waitForUrlContains("cart.html");
        return new CartPage(driver);

    }
    public boolean isAddToCartButtonDisplayed(String itemName) {
        By buttonLocator = By.id("add-to-cart-" + itemName.toLowerCase().replace(" ", "-"));
        try {
            return waitUtils.waitForVisibility(buttonLocator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    public InventoryPage selectSortOption(String visibleText) {
        WebElement dropdownElement = waitUtils.waitForVisibility(sortDropdown);
        Select select = new Select(dropdownElement);
        select.selectByVisibleText(visibleText);
        return this;
    }

    public List<Double> getAllItemPrices() {
        return driver.findElements(itemPrices)
                .stream()
                .map(element -> Double.parseDouble(element.getText().replace("$", "").trim()))
                .collect(Collectors.toList());
    }

    public List<String> getAllItemNames() {
        return driver.findElements(itemTitles)
                .stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }
    public boolean areAllItemImagesUsingFallback() {
        waitUtils.waitForVisibility(itemImages);
        List<WebElement> images = driver.findElements(itemImages);
        return images.stream().allMatch(img -> img.getAttribute("src").contains("sl-404"));
    }
    public InventoryPage openMenu() {
        waitUtils.waitForClickable(menuButton).click();
        return this;
    }

    public LoginPage clickLogout() {
        waitUtils.waitForClickable(logoutLink).click();
        waitUtils.waitForUrlContains("saucedemo.com");
        return new LoginPage(driver);
    }
}