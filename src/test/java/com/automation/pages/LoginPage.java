package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Page object for the SauceDemo login screen (https://www.saucedemo.com). */
public class LoginPage {
    private final WebDriver driver;
    private final WaitUtils waitUtils;
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public LoginPage enterUsername(String username) {
       WebElement element = waitUtils.waitForVisibility(usernameField);
       element.clear();
       element.sendKeys(username);
       return this;

    }
    public LoginPage enterPassword(String password) {
        WebElement element = waitUtils.waitForVisibility(passwordField);
        element.clear();
        element.sendKeys(password);
        return this;
    }
    public void clickLoginButton() {
        waitUtils.waitForClickable(loginButton).click();
    }
    public void login(String username, String password) {
        enterUsername(username).enterPassword(password).clickLoginButton();
    }
    public String getErrorMessage() {
      return waitUtils.waitForVisibility(errorMessage).getText().trim();

    }
    /** Returns false (rather than throwing) if the error banner does not appear. */
    public boolean isErrorMessageDisplayed() {
        try {
            return waitUtils.waitForVisibility(errorMessage).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }

    }

}
