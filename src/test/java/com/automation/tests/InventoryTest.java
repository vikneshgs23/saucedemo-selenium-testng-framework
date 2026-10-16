package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.InventoryPage;
import com.automation.pages.LoginPage;
import com.automation.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    // Runs after BaseTest.setUp() (superclass @BeforeMethod methods run first),
    // so the browser is already open on the login page.
    @BeforeMethod(alwaysRun = true)
    public void loginAndOpenInventory() {
        new LoginPage(driver).login(
                ConfigReader.getStandardUsername(),
                ConfigReader.getPassword());
        inventoryPage = new InventoryPage(driver);
    }

    @Test(description = "Verify inventory page loads with title and 6 products")
    public void testInventoryPageLoaded() {
        Assert.assertEquals(inventoryPage.getPageTitle(), "Products",
                "Inventory page title mismatch");
        Assert.assertEquals(inventoryPage.getItemCount(), 6,
                "Unexpected number of catalog items");
    }

    @Test(description = "Verify adding and removing the backpack updates the cart badge")
    public void testAddAndRemoveBackpack() {
        inventoryPage.addBackpackToCart();
        Assert.assertEquals(inventoryPage.getCartBadgeCount(), "1",
                "Cart badge should show 1 after adding the backpack");

        inventoryPage.removeBackpackFromCart();
        Assert.assertFalse(inventoryPage.isCartBadgeDisplayed(),
                "Cart badge should disappear after removing the backpack");
    }
}
