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
        LoginPage loginPage = new LoginPage(getDriver());
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
        LoginPage loginPage = new LoginPage(getDriver());
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

        // Step 7: Click 'Continue Shopping'
        inventoryPage = cartPage.clickContinueShopping();

        // Step 8: Verify the removed item shows 'Add to cart' on the inventory page
        Assert.assertTrue(inventoryPage.isAddToCartButtonDisplayed(BIKE_LIGHT),
                "Removed item '" + BIKE_LIGHT + "' should toggle back to 'Add to cart' button");
    }

    @Test(
            testName = "TC_E2E_003",
            description = "TC_E2E_003 - Verify catalog sorting by price ascending, price descending, and alphabetical descending"
    )
    public void TC_E2E_003_verifyProductSortingFilters() {
        // Step 1: Log in with valid credentials
        LoginPage loginPage = new LoginPage(getDriver());
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
        LoginPage loginPage = new LoginPage(getDriver());
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
        stepOnePage.clickContinueExpectingFailure();
        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(), "Error message should appear for empty first name");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("First Name is required"));

        // Step 4: Fill First Name, leave Last Name empty, click 'Continue'
        stepOnePage.dismissErrorMessageIfPresent()
                .enterFirstName("John")
                .clearLastName()
                .enterPostalCode("600001");
        stepOnePage.clickContinueExpectingFailure();

        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(), "Error message should appear for empty last name");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("Last Name is required"));

        // Step 5: Fill Last Name, leave Postal Code empty, click 'Continue'
        stepOnePage.dismissErrorMessageIfPresent()
                .enterFirstName("John")
                .enterLastName("Doe")
                .clearPostalCode();
        // Changed to clickContinueExpectingFailure() to prevent timeout on step two transition
        stepOnePage.clickContinueExpectingFailure();

        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(), "Error message should appear for empty postal code");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("Postal Code is required"));
    }

    @Test(
            testName = "TC_E2E_005",
            description = "TC_E2E_005 - Verify cancellation flows from Checkout Step One (returns to Cart with item preserved) and Checkout Step Two (returns to Inventory)"
    )
    public void TC_E2E_005_verifyCheckoutCancellationFlows() {
        final String FLEECE_JACKET = "Sauce Labs Fleece Jacket";

        // Step 1: Log in and add 'Sauce Labs Fleece Jacket' to the cart
        LoginPage loginPage = new LoginPage(getDriver());
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");
        inventoryPage.addItemToCart(FLEECE_JACKET);
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1", "Cart badge count mismatch");

        // Step 2: Navigate to Cart and click 'Checkout'
        CartPage cartPage = inventoryPage.clickCart();
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart", "Failed to reach Cart page");
        Assert.assertTrue(cartPage.isItemInCart(FLEECE_JACKET), FLEECE_JACKET + " should be in the cart");

        CheckoutStepOnePage stepOnePage = cartPage.clickCheckout();
        Assert.assertEquals(stepOnePage.getPageTitle(), "Checkout: Your Information",
                "Failed to reach Checkout Step One");

        // Step 3: Click 'Cancel' on the 'Checkout: Your Information' page
        cartPage = stepOnePage.clickCancel();

        // Step 4: Verify navigation returns to 'Your Cart' with the item preserved
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart",
                "Canceling step one should return user to Cart page");
        Assert.assertEquals(cartPage.getCartItemCount(), 1,
                "Cart count should remain 1 after cancellation");
        Assert.assertTrue(cartPage.isItemInCart(FLEECE_JACKET),
                FLEECE_JACKET + " should still be present in the cart");

        // Step 5: Click 'Checkout', fill required details, and click 'Continue'
        stepOnePage = cartPage.clickCheckout();
        stepOnePage.fillCustomerInformation("G S", "Viknesh", "600001");

        CheckoutStepTwoPage stepTwoPage = stepOnePage.clickContinue();
        Assert.assertEquals(stepTwoPage.getPageTitle(), "Checkout: Overview",
                "Failed to reach Checkout Overview page");

        // Step 6: On 'Checkout: Overview', click 'Cancel'
        inventoryPage = stepTwoPage.clickCancel();

        // Verify cancellation from overview redirects back to the inventory catalog
        Assert.assertEquals(inventoryPage.getPageTitle(), "Products",
                "Canceling step two overview should return user to Inventory page");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should contain inventory.html");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1",
                "Item should remain preserved in cart badge after overview cancellation");
    }

    @Test(
            testName = "TC_E2E_006",
            description = "TC_E2E_006 - Verify problem_user defect behavior: broken product images and unfillable Last Name field triggering validation error"
    )
    public void TC_E2E_006_verifyProblemUserKnownDefects() {
        // 1. Log in with problem_user
        LoginPage loginPage = new LoginPage(getDriver());
        InventoryPage inventoryPage = loginPage.login("problem_user", ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");

        // 2. Inspect product images on the catalog (known bug: all images show fallback 404 dog photo)
        Assert.assertTrue(inventoryPage.areAllItemImagesUsingFallback(),
                "Expected all product images to render fallback asset for problem_user");

        // 3. Add 'Sauce Labs Backpack' to the cart
        inventoryPage.addItemToCart("Sauce Labs Backpack");
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1", "Cart badge count mismatch");

        // 4. Navigate to checkout and attempt to fill 'Last Name'
        CartPage cartPage = inventoryPage.clickCart();
        CheckoutStepOnePage stepOnePage = cartPage.clickCheckout();

        stepOnePage.enterFirstName("John")
                .enterLastName("Doe")
                .enterPostalCode("600001");

        // 5. Click 'Continue' and handle the known defect:
        // On problem_user, the Last Name field cannot be updated, causing the page to fail validation
        stepOnePage.clickContinueExpectingFailure();

        // Validate that the system rejects the submission with the Last Name error
        Assert.assertTrue(stepOnePage.isErrorMessageDisplayed(),
                "Error banner should be visible due to unfillable Last Name field");
        Assert.assertTrue(stepOnePage.getErrorMessage().contains("Last Name is required"),
                "Expected 'Error: Last Name is required' banner due to problem_user field defect");

        // Confirm the URL remains on step one rather than advancing to overview
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-one.html"),
                "User should remain on checkout-step-one page due to validation halt");
    }

    @Test(
            testName = "TC_E2E_007",
            description = "TC_E2E_007 - Verify session termination on logout, browser back-button denial, and direct URL routing security"
    )
    public void TC_E2E_007_verifyLogoutAndUnauthorizedRouteAccess() {
        // Step 1: Log in, add two items to the cart
        LoginPage loginPage = new LoginPage(getDriver());
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");

        inventoryPage.addItemToCart(BACKPACK)
                .addItemToCart(BIKE_LIGHT);
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "2", "Cart badge count mismatch");

        // Step 2 & 3: Open the burger menu sidebar and click 'Logout'
        loginPage = inventoryPage.openMenu()
                .clickLogout();

        // Step 4: Verify user is on the login page
        Assert.assertTrue(loginPage.isAtLoginPage(), "User was not redirected to the login page after logout");
        Assert.assertFalse(getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should not point to inventory after logout");

        // Step 5: Click the browser's Back button
        getDriver().navigate().back();

        // Verify session protection kicks in: user remains barred from catalog
        Assert.assertTrue(loginPage.isAtLoginPage(),
                "Browser back button allowed unauthorized access into inventory");
        Assert.assertTrue(loginPage.getErrorMessage().contains("You can only access '/inventory.html' when you are logged in."),
                "Expected access restriction error after pressing back");

        // Step 6: Attempt to access https://www.saucedemo.com/inventory.html directly via the URL bar
        getDriver().get("https://www.saucedemo.com/inventory.html");

        // Verify direct route access redirects back to login with active restriction banner
        Assert.assertTrue(loginPage.isAtLoginPage(),
                "Direct deep-linking to /inventory.html allowed unauthorized access");
        Assert.assertTrue(loginPage.getErrorMessage().contains("You can only access '/inventory.html' when you are logged in."),
                "Expected route authorization error banner when deep-linking without active session");
    }
    @Test(
            testName = "TC_E2E_008",
            description = "TC_E2E_008 - Verify checkout behavior when attempting to checkout with an empty cart"
    )
    public void TC_E2E_008_verifyEmptyCartCheckoutBehavior() {
        // Step 1: Log in with valid credentials
        LoginPage loginPage = new LoginPage(getDriver());
        InventoryPage inventoryPage = loginPage.login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products", "Failed to reach Inventory page");

        // Step 2: Ensure the cart is empty (badge is not displayed)
        Assert.assertFalse(inventoryPage.isCartBadgeDisplayed(),
                "Cart badge should not be displayed when cart is empty");

        // Step 3: Navigate to the Cart page
        CartPage cartPage = inventoryPage.clickCart();
        Assert.assertEquals(cartPage.getPageTitle(), "Your Cart", "Failed to reach Cart page");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart item count should be 0");

        // Step 4: Click 'Checkout' on an empty cart
        CheckoutStepOnePage stepOnePage = cartPage.clickCheckout();

        // Verification: Documenting application behavior
        // Note: SauceDemo contains a known UX gap where it permits navigating to Checkout Step One even with 0 items
        Assert.assertEquals(stepOnePage.getPageTitle(), "Checkout: Your Information",
                "Application navigated to Checkout Step One despite cart being empty (known behavior/defect)");
        Assert.assertTrue(getDriver().getCurrentUrl().contains("checkout-step-one.html"),
                "URL should contain checkout-step-one.html");
    }
    @Test(
            testName = "TC_E2E_009",
            description = "TC_E2E_009 - Verify unauthorized direct URL navigation to protected pages redirects to login with an error banner"
    )
    public void TC_E2E_009_verifyUnauthenticatedDirectRouteAccess() {
        LoginPage loginPage = new LoginPage(getDriver());

        // 1. Attempt direct navigation to protected inventory page
        getDriver().get("https://www.saucedemo.com/inventory.html");

        // Verify redirection to login page and access denied error message
        Assert.assertTrue(loginPage.isAtLoginPage(),
                "Direct navigation to /inventory.html should redirect unauthenticated user to login");
        Assert.assertFalse(getDriver().getCurrentUrl().contains("inventory.html"),
                "URL should not remain on inventory.html");
        Assert.assertTrue(loginPage.getErrorMessage().contains("You can only access '/inventory.html' when you are logged in."),
                "Error banner mismatch for /inventory.html access restriction");

        // 2. Attempt direct navigation to protected checkout-step-one page
        getDriver().get("https://www.saucedemo.com/checkout-step-one.html");

        // Verify redirection to login page and access denied error message for checkout
        Assert.assertTrue(loginPage.isAtLoginPage(),
                "Direct navigation to /checkout-step-one.html should redirect unauthenticated user to login");
        Assert.assertFalse(getDriver().getCurrentUrl().contains("checkout-step-one.html"),
                "URL should not remain on checkout-step-one.html");
        Assert.assertTrue(loginPage.getErrorMessage().contains("You can only access '/checkout-step-one.html' when you are logged in."),
                "Error banner mismatch for /checkout-step-one.html access restriction");
    }
}