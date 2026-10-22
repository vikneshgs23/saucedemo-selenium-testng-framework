package com.automation.pages;

import com.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckoutStepTwoPage {

    private final WebDriver driver;
    private final WaitUtils waitUtils;

    // Locators
    private final By pageTitle = By.className("title");
    private final By subtotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.className("summary_total_label");
    private final By finishButton = By.id("finish");
    private final By cancelButton = By.id("cancel");
    private final By completeHeader = By.className("complete-header");

    public CheckoutStepTwoPage(WebDriver driver) {
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
    }

    public String getPageTitle() {
        waitUtils.waitForUrlContains("checkout-step-two.html");
        return waitUtils.getText(pageTitle);
    }

    public String getItemTotalText() {
        return waitUtils.waitForVisibility(subtotalLabel).getText().trim();
    }

    public String getTotalText() {
        return waitUtils.waitForVisibility(totalLabel).getText().trim();
    }

    public CheckoutStepTwoPage clickFinish() {
        waitUtils.waitForClickable(finishButton).click();
        return this;
    }

    public String getOrderCompleteMessage() {
        return waitUtils.waitForVisibility(completeHeader).getText().trim();
    }

    public InventoryPage clickCancel() {
        waitUtils.waitForClickable(cancelButton).click();
        return new InventoryPage(driver);
    }
}