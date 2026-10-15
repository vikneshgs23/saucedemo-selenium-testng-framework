package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.LoginPage;
import com.automation.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "Valid user can log in and lands on the inventory page")
    public void ValidUserLogin(){
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getStandardUsername(), ConfigReader.getPassword());
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory.html"),"Expected to land in Inventory after Login");
        Assert.assertFalse(loginPage.isErrorMessageDisplayed(),"No Error Message Due to Valid Login");
    }
}

