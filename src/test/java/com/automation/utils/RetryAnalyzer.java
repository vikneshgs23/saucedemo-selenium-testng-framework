package com.automation.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 1; // Re-run failed test at most 1 time

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (retryCount < MAX_RETRY_COUNT) {
                retryCount++;
                result.setStatus(ITestResult.FAILURE); // Mark current attempt as failed
                return true;                           // Tell TestNG to re-run the test
            } else {
                result.setStatus(ITestResult.FAILURE); // Max retries reached; fail permanently
            }
        } else {
            result.setStatus(ITestResult.SUCCESS);
        }
        return false;
    }
}