package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.*;

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
    private final By errorCloseButton = By.cssSelector(".error-button");

    public CheckoutStepOnePage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        WebElement element = waitUtils.waitForVisibility(firstNameInput);
        element.clear();
        element.sendKeys(firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        WebElement element = waitUtils.waitForVisibility(lastNameInput);
        element.clear();
        element.sendKeys(lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        WebElement element = waitUtils.waitForVisibility(postalCodeInput);
        element.clear();
        element.sendKeys(postalCode);
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
    public CheckoutStepOnePage clearFirstName() {
        WebElement element = waitUtils.waitForVisibility(firstNameInput);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        return this;
    }

    public CheckoutStepOnePage clearLastName() {
        WebElement element = waitUtils.waitForVisibility(lastNameInput);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        return this;
    }

    public CheckoutStepOnePage clearPostalCode() {
        WebElement element = waitUtils.waitForVisibility(postalCodeInput);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        return this;
    }
    public CheckoutStepOnePage dismissErrorMessageIfPresent() {
        try {
            driver.findElement(errorCloseButton).click();
        } catch (Exception ignored) {
            // No banner was present
        }
        return this;
    }
}
