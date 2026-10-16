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

    // The badge is absent from the DOM when the cart is empty, so a missing element means false
    public boolean isCartBadgeDisplayed() {
        try {
            return driver.findElement(cartBadge).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }

    public void clickCart() {
        waitUtils.waitForClickable(cartLink).click();
    }
}
