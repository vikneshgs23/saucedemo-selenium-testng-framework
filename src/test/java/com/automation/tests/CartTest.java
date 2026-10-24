package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.CartPage;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import com.automation.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";

    private InventoryPage inventoryPage;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeEachTest() {
        new LoginPage(getDriver()).login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());
        inventoryPage = new InventoryPage(getDriver());
    }

    @Test(description = "Item added from the inventory page appears in the cart")
    public void testItemAddedToCartSuccessfully() {
        inventoryPage.addItemToCart(BACKPACK);
        CartPage cartPage = inventoryPage.clickCart();

        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart", "Cart page title mismatch");
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Expected 1 item in the cart");
        Assert.assertTrue(cartPage.isItemInCart(BACKPACK), BACKPACK + " should be in the cart");
    }

    @Test(description = "Removing the only item leaves the cart empty")
    public void testRemoveItemFromCart() {
        inventoryPage.addItemToCart(BACKPACK);
        CartPage cartPage = inventoryPage.clickCart();

        cartPage.removeItem(BACKPACK);

        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart should be empty after item removal");
        Assert.assertFalse(cartPage.isItemInCart(BACKPACK), BACKPACK + " should no longer be in the cart");
    }

    @Test(description = "Continue Shopping returns to the inventory page")
    public void testContinueShoppingNavigation() {
        inventoryPage.addItemToCart(BACKPACK);
        CartPage cartPage = inventoryPage.clickCart();

        cartPage.clickContinueShopping();

        Assert.assertTrue(getDriver().getCurrentUrl().contains("inventory.html"),
                "Expected to be redirected back to the inventory page");
    }
}