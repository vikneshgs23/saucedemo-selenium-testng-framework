package com.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ScreenshotUtil {

    private static final String SCREENSHOT_DIR = "target/screenshots";

    public static String captureScreenshot(WebDriver driver, String testMethodName) {
        if (driver == null) {
            System.err.println("Driver instance was null. Skipping screenshot capture.");
            return null;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File sourceFile = ts.getScreenshotAs(OutputType.FILE);

            // Create target/screenshots directory if it doesn't exist
            Path destinationDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(destinationDir);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testMethodName + "_" + timestamp + ".png";
            Path destinationPath = destinationDir.resolve(fileName);

            Files.copy(sourceFile.toPath(), destinationPath);
            return destinationPath.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Captures one screenshot, saves a PNG copy to target/screenshots (for CI artifacts),
     * and returns the image as a Base64 string so it can be embedded directly in the
     * Extent report. Embedded images keep the report self-contained, so screenshots still
     * display when the report is hosted on GitHub Pages.
     */
    public static String captureBase64(WebDriver driver, String testMethodName) {
        if (driver == null) {
            System.err.println("Driver instance was null. Skipping screenshot capture.");
            return null;
        }

        try {
            byte[] imageBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

            Path destinationDir = Paths.get(SCREENSHOT_DIR);
            Files.createDirectories(destinationDir);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testMethodName + "_" + timestamp + ".png";
            Files.write(destinationDir.resolve(fileName), imageBytes);

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException | WebDriverException e) {
            System.err.println("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }
}