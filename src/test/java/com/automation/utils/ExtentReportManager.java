package com.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final String REPORT_PATH = "test-output/ExtentReport.html";

    // ThreadLocal storage so concurrent tests do not overwrite each other's logs
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            new File("test-output").mkdirs();

            ExtentSparkReporter spark = new ExtentSparkReporter(REPORT_PATH);
            spark.config().setDocumentTitle("SauceDemo Automation Execution Report");
            spark.config().setReportName("Functional UI Test Automation Results");
            spark.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Add environment metadata
            extent.setSystemInfo("Application", "SauceDemo");
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
        }
        return extent;
    }

    public static ExtentTest getTest() {
        return testThreadLocal.get();
    }

    public static void setTest(ExtentTest test) {
        testThreadLocal.set(test);
    }

    public static void unloadTest() {
        testThreadLocal.remove();
    }
}