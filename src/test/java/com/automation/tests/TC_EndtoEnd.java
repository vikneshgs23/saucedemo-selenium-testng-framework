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

    @Test(
            testName = "TC_E2E_003",
            description = "TC_E2E_003 - Verify catalog sorting by price ascending, price descending, and alphabetical descending"
    )
    public void TC_E2E_003_verifyProductSortingFilters() {
        // Step 1: Log in with valid credentials
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to navigate to Inventory page");

        // Step 2: Select sort filter 'Price (low to high)'
        inventoryPage.selectSortOption("Price (low to high)");

        // Step 3: Verify inventory list sorts prices in ascending order
        java.util.List<Double> lowToHighPrices = inventoryPage.getAllItemPrices();
        java.util.List<Double> sortedLowToHigh = new java.util.ArrayList<>(lowToHighPrices);
        java.util.Collections.sort(sortedLowToHigh);

        Assert.assertEquals(lowToHighPrices, sortedLowToHigh,
                "Prices are not sorted in ascending order (low to high)");

        // Step 4: Select sort filter 'Price (high to low)'
        inventoryPage.selectSortOption("Price (high to low)");

        // Step 5: Verify inventory list sorts prices in descending order
        java.util.List<Double> highToLowPrices = inventoryPage.getAllItemPrices();
        java.util.List<Double> sortedHighToLow = new java.util.ArrayList<>(highToLowPrices);
        sortedHighToLow.sort(java.util.Collections.reverseOrder());

        Assert.assertEquals(highToLowPrices, sortedHighToLow,
                "Prices are not sorted in descending order (high to low)");

        // Step 6: Select sort filter 'Name (Z to A)'
        inventoryPage.selectSortOption("Name (Z to A)");

        // Step 7: Verify product names sort alphabetically descending
        java.util.List<String> namesZtoA = inventoryPage.getAllItemNames();
        java.util.List<String> sortedNamesZtoA = new java.util.ArrayList<>(namesZtoA);
        sortedNamesZtoA.sort(java.util.Collections.reverseOrder());

        Assert.assertEquals(namesZtoA, sortedNamesZtoA,
                "Product titles are not sorted in reverse alphabetical order (Z to A)");
    }
    @Test(
            testName = "TC_E2E_004",
            description = "TC_E2E_004 - Verify checkout step one field-level validation errors for missing first name, last name, and postal code"
    )
    public void TC_E2E_004_verifyCheckoutFieldValidations() {
        // Step 1: Log in and add an item to the cart
        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");
        inventoryPage.addItemToCart(BACKPACK);

        // Step 2: Navigate to cart and click 'Checkout'
        CartPage cartPage = inventoryPage.clickCart();
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart", "Failed to reach Cart page");

        CheckoutStepOnePage stepOnePage = cartPage.clickCheckout();

// Step 3: Leave First Name empty, fill other fields, click 'Continue'
        stepOnePage.clearFirstName()
                .enterLastName("Doe")
                .enterPostalCode("600001");
        stepOnePage.clickContinue();

        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(), "Error message should appear for empty first name");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("First Name is required"));

        // Step 4: Fill First Name, leave Last Name empty, click 'Continue'
        stepOnePage.dismissErrorMessageIfPresent()
                .enterFirstName("John")
                .clearLastName()
                .enterPostalCode("600001");
        stepOnePage.clickContinue();

        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(), "Error message should appear for empty last name");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("Last Name is required"));

        // Step 5: Fill Last Name, leave Postal Code empty, click 'Continue'
        stepOnePage.dismissErrorMessageIfPresent()
                .enterFirstName("John")
                .enterLastName("Doe")
                .clearPostalCode();
        stepOnePage.clickContinue();

        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(), "Error message should appear for empty postal code");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("Postal Code is required"));
    }
}