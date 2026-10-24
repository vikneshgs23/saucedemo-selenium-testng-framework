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

public class TestListener implements ITestListener {

    private static final ExtentReports extent = ExtentReportManager.getInstance();

    @Override
    public void onStart(ITestContext context) {
        System.out.println("=== Test Suite Execution Started: " + context.getName() + " ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        System.out.println(">>> Starting Test: " + testName);
        ExtentTest test = extent.createTest(testName, result.getMethod().getDescription());
        ExtentReportManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("PASSED: " + result.getMethod().getMethodName());
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.PASS, "Test executed successfully.");
        }
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        System.err.println("FAILED: " + methodName);

        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.FAIL, result.getThrowable());

            // Retrieve thread-isolated WebDriver instance directly from BaseTest
            WebDriver driver = BaseTest.getDriver();

            if (driver != null) {
                String screenshotPath = ScreenshotUtil.captureScreenshot(driver, methodName);
                if (screenshotPath != null) {
                    ExtentReportManager.getTest().fail("Failure Screenshot",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                }
            } else {
                System.err.println("WebDriver was null for thread; cannot capture screenshot for " + methodName);
            }
        }
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("SKIPPED: " + result.getMethod().getMethodName());
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
        }
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("=== Test Suite Execution Finished: " + context.getName() + " ===");
        // Write HTML report to disk
        extent.flush();
    }
}