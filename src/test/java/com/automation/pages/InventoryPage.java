package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

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
        waitUtils.waitForClickable(cartLink).click();
        return new CartPage(driver);
    }
}