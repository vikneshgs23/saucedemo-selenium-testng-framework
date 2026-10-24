package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.*;

public class CheckoutStepOnePage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // Locators
    private final By firstNameInput = By.id("first-name");
    private final By pageTitle = By.className("title");
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

    private void safeType(By locator, String value) {
        WebElement element = waitUtils.waitForVisibility(locator);
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        element.sendKeys(value);
    }

    public CheckoutStepOnePage enterFirstName(String firstName) {
        safeType(firstNameInput, firstName);
        return this;
    }

    public CheckoutStepOnePage enterLastName(String lastName) {
        safeType(lastNameInput, lastName);
        return this;
    }

    public CheckoutStepOnePage enterPostalCode(String postalCode) {
        safeType(postalCodeInput, postalCode);
        return this;
    }

    public CheckoutStepOnePage fillCustomerInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        return this;
    }

    /**
     * Clicks continue and waits for the transition to Step Two.
     * Use this ONLY when valid customer info is submitted (Happy path / E2E_001).
     */
    public CheckoutStepTwoPage clickContinue() {
        clickContinueButtonOnly();
        waitUtils.waitForUrlContains("checkout-step-two.html");
        return new CheckoutStepTwoPage(driver);
    }

    /**
     * Clicks continue WITHOUT waiting for URL transition.
     * Use this for negative validation tests (missing fields, problem_user) where
     * the browser is expected to stay on Step One to show error messages.
     */
    public CheckoutStepOnePage clickContinueExpectingFailure() {
        clickContinueButtonOnly();
        return this;
    }

    private void clickContinueButtonOnly() {
        WebElement continueBtn = waitUtils.waitForClickable(continueButton);
        try {
            continueBtn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", continueBtn);
        }
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

    public String getPageTitle() {
        waitUtils.waitForUrlContains("checkout-step-one.html");
        return waitUtils.getText(pageTitle);
    }
}