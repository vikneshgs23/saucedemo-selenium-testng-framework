package com.automation.utils;

import com.automation.base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.reflect.Field;

public class TestListener implements ITestListener {

    private static final ExtentReports extent = ExtentReportManager.getInstance();
    // ThreadLocal ensures parallel test runs do not overwrite each other's report logs
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        System.out.println("=== Test Suite Execution Started: " + context.getName() + " ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println(">>> Starting Test: " + testName);
        ExtentTest test = extent.createTest(testName, result.getMethod().getDescription());
        testNode.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASSED: " + result.getMethod().getMethodName());
        testNode.get().log(Status.PASS, "Test executed successfully.");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        System.err.println("FAILED: " + methodName);
        testNode.get().log(Status.FAIL, result.getThrowable());

        // Extract driver instance from BaseTest
        Object currentClass = result.getInstance();
        WebDriver driver = null;

        try {
            Field driverField = BaseTest.class.getDeclaredField("driver");
            driverField.setAccessible(true);
            driver = (WebDriver) driverField.get(currentClass);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Could not extract driver from test class: " + e.getMessage());
        }

        if (driver != null) {
            String screenshotPath = ScreenshotUtil.captureScreenshot(driver, methodName);
            if (screenshotPath != null) {
                // Attach screenshot into the Extent Report card
                testNode.get().fail("Failure Screenshot",
                        MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("SKIPPED: " + result.getMethod().getMethodName());
        testNode.get().log(Status.SKIP, "Test skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== Test Suite Execution Finished: " + context.getName() + " ===");
        // Write the HTML report to disk
        extent.flush();
    }
}