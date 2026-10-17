package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Page object for the SauceDemo cart screen (https://www.saucedemo.com/cart.html). */
public class CartPage {
    private final WebDriver driver;
    private final WaitUtils waitUtils;

    private final By pageTitle = By.className("title");
    private final By cartItems = By.className("cart_item");
    private final By itemNames = By.className("inventory_item_name");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public String getPageTitle() {
        return waitUtils.waitForVisibility(pageTitle).getText().trim();
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public boolean isItemInCart(String itemName) {
        List<WebElement> names = driver.findElements(itemNames);
        return names.stream().anyMatch(e -> e.getText().trim().equals(itemName));
    }

    public void removeItem(String itemName) {
        waitUtils.waitForClickable(removeButtonFor(itemName)).click();
    }

    public void clickCheckout() {
        waitUtils.waitForClickable(checkoutButton).click();
    }

    public void clickContinueShopping() {
        waitUtils.waitForClickable(continueShoppingButton).click();
    }

    /** Builds the remove button locator, e.g. "Sauce Labs Backpack" -> remove-sauce-labs-backpack. */
    private By removeButtonFor(String itemName) {
        return By.id("remove-" + itemName.toLowerCase().replace(" ", "-"));
    }
}