package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.CartPage;
import com.automation.pages.CheckoutStepOnePage;
import com.automation.pages.CheckoutStepTwoPage;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import com.automation.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    private static final String ITEM_NAME = "Sauce Labs Backpack";
    private CartPage cartPage;

    @BeforeMethod(alwaysRun = true)
    public void setupCartWithItem() {
        InventoryPage inventoryPage = new LoginPage(driver).login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());

        inventoryPage.addItemToCart(ITEM_NAME);
        cartPage = inventoryPage.clickCart();
    }

    @Test(description = "Completing the entire checkout workflow displays the confirmation message")
    public void testSuccessfulCheckout() {
        CheckoutStepOnePage stepOne = cartPage.clickCheckout();
        stepOne.fillCustomerInformation("John", "Doe", "600001");

        CheckoutStepTwoPage stepTwo = stepOne.clickContinue();
        Assert.assertEquals(stepTwo.getPageTitle(), "Checkout: Overview", "Overview title mismatch");
        Assert.assertTrue(stepTwo.getItemTotalText().contains("29.99"), "Subtotal should reflect item price");

        stepTwo.clickFinish();
        Assert.assertEquals(stepTwo.getOrderCompleteMessage(), "Thank you for your order!",
                "Order confirmation header mismatch");
    }

    @Test(description = "Submitting step one without postal code shows an error message")
    public void testMissingPostalCodeValidationError() {
        CheckoutStepOnePage stepOne = cartPage.clickCheckout();
        stepOne.enterFirstName("John")
                .enterLastName("Doe");

        stepOne.clickContinue();

        Assert.assertTrue(stepOne.isErrorMessageDisplayed(), "Error message should be visible");
        Assert.assertTrue(stepOne.getErrorMessage().contains("Postal Code is required"),
                "Error message should mention Postal Code");
    }
}