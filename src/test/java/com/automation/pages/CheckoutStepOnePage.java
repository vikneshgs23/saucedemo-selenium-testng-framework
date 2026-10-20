package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class CheckoutStepOnePage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // Locators
    private final By firstNameInput = By.id("first-name");
    private final By lastNameInput = By.id("last-name");
    private final By postalCodeInput = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public CheckoutStepOnePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        waitUtils.waitForVisibility(firstNameInput).clear();
        driver.findElement(firstNameInput).sendKeys(firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        waitUtils.waitForVisibility(lastNameInput).clear();
        driver.findElement(lastNameInput).sendKeys(lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        waitUtils.waitForVisibility(postalCodeInput).clear();
        driver.findElement(postalCodeInput).sendKeys(postalCode);
        return this;
    }

    public CheckoutStepOnePage fillCustomerInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return this;
    }

    public CheckoutStepTwoPage clickContinue() {
        waitUtils.waitForClickable(continueButton).click();
        return new CheckoutStepTwoPage(driver);
    }

    public CartPage clickCancel() {
        waitUtils.waitForClickable(cancelButton).click();
        return new CartPage(driver);
    }

    public String getErrorMessage() {
        return waitUtils.waitForVisibility(errorMessage).getText().trim();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return driver.findElement(errorMessage).isDisplayed();
        } catch (NoSuchElementException | TimeoutException e) {
            return false;
        }
    }
}
