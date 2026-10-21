package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.*;
import com.automation.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TC_EndtoEnd extends BaseTest {

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final String BOLT_TSHIRT = "Sauce Labs Bolt T-Shirt";

    // Item prices on SauceDemo: Backpack = $29.99, Bike Light = $9.99
    private static final double EXPECTED_SUBTOTAL = 39.98;

    @Test(
            testName = "TC_E2E_001",
            description = "TC_E2E_001 - Verify complete end-to-end purchase flow with multiple items, subtotal calculation, tax, and order confirmation"
    )
    public void TC_E2E_001_verifySuccessfulMultiItemOrder() {
        // Step 1: Log in with valid credentials
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");

        // Step 2: Add 'Sauce Labs Backpack' and 'Sauce Labs Bike Light' to cart
        inventoryPage.addItemToCart(BACKPACK)
                .addItemToCart(BIKE_LIGHT);

        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "2",
                "Cart badge count mismatch after adding items");

        // Step 3: Click the shopping cart icon
        CartPage cartPage = inventoryPage.clickCart();
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart", "Failed to reach Cart page");

        // Step 4: Verify selected items are listed, then click 'Checkout'
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Cart item count should be 2");
        Assert.assertTrue(cartPage.isItemInCart(BACKPACK), BACKPACK + " should be in the cart");
        Assert.assertTrue(cartPage.isItemInCart(BIKE_LIGHT), BIKE_LIGHT + " should be in the cart");

        CheckoutStepOnePage stepOnePage = cartPage.clickCheckout();

        // Step 5: Enter First Name, Last Name, and Postal Code
        stepOnePage.fillCustomerInformation("G S", "Viknesh", "600001");

        // Step 6: Click 'Continue'
        CheckoutStepTwoPage stepTwoPage = stepOnePage.clickContinue();
        Assert.assertEquals(stepTwoPage.getPageTitle(), "Checkout: Overview", "Failed to reach Checkout Overview page");

        // Step 7: Verify item total, tax, and total amount on 'Checkout: Overview'
        String itemTotalText = stepTwoPage.getItemTotalText(); // e.g. "Item total: $39.98"
        String totalText = stepTwoPage.getTotalText();         // e.g. "Total: $43.18"

        double actualSubtotal = Double.parseDouble(itemTotalText.replaceAll("[^0-9.]", ""));
        double actualTotal = Double.parseDouble(totalText.replaceAll("[^0-9.]", ""));

        Assert.assertEquals(actualSubtotal, EXPECTED_SUBTOTAL, 0.001, "Calculated item subtotal mismatch");
        Assert.assertTrue(actualTotal > actualSubtotal, "Total amount should be greater than subtotal to include tax");

        // Step 8: Click 'Finish' and verify confirmation
        stepTwoPage.clickFinish();
        Assert.assertEquals(stepTwoPage.getOrderCompleteMessage(), "Thank you for your order!",
                "Order completion confirmation text mismatch");
    }
    @Test(
            testName = "TC_E2E_002",
            description = "TC_E2E_002 - Verify adding three items, removing one from cart, badge count update, and inventory button state reflection"
    )
    public void TC_E2E_002_verifyCartModificationAndStateSync() {
        // 1. Log in with valid credentials
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");

        // 2. Add three items to the cart from the inventory page
        inventoryPage.addItemToCart(BACKPACK)
                .addItemToCart(BIKE_LIGHT)
                .addItemToCart(BOLT_TSHIRT);

        // 3. Verify cart badge displays '3'
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "3",
                "Cart badge count should display '3' after adding three items");

        // 4. Click the cart icon to view cart
        CartPage cartPage = inventoryPage.clickCart();
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart", "Failed to reach Cart page");
        Assert.assertEquals(cartPage.getCartItemCount(), 3, "Cart should contain 3 items");

        // 5. Click 'Remove' on one item
        cartPage.removeItem(BIKE_LIGHT);

        // 6. Verify cart badge updates to '2'
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "2",
                "Cart badge should display '2' after removing one item");
        Assert.assertFalse(cartPage.isItemInCart(BIKE_LIGHT),
                BIKE_LIGHT + " should no longer appear in the cart list");

        // 7. Click 'Continue Shopping'
        cartPage.clickContinueShopping();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),
                "User should be navigated back to the Inventory page");

        // 8. Verify the removed item shows 'Add to cart' on the inventory page
        Assert.assertTrue(inventoryPage.isAddToCartButtonDisplayed(BIKE_LIGHT),
                "Removed item '" + BIKE_LIGHT + "' should toggle back to 'Add to cart' button");
    }
}