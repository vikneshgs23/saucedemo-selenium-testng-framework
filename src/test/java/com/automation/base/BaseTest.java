package com.automation.base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {

    // ThreadLocal container: gives each thread its own isolated WebDriver instance
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    protected static final String BASE_URL = "https://www.saucedemo.com/";

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static void unloadDriver() {
        driverThreadLocal.remove();
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        // Read browser and headless parameters passed from Maven or default to chrome/headed
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        WebDriver localDriver;

        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (isHeadless) {
                    firefoxOptions.addArguments("-headless");
                }
                localDriver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                // Suppress the password manager / data-breach popup
                edgeOptions.setExperimentalOption("prefs", getPasswordManagerPrefs());
                edgeOptions.addArguments("--disable-features=PasswordLeakDetection");
                edgeOptions.addArguments("--incognito");
                if (isHeadless) {
                    edgeOptions.addArguments("--headless=new");
                    edgeOptions.addArguments("--window-size=1920,1080");
                    edgeOptions.addArguments("--disable-gpu");
                    edgeOptions.addArguments("--no-sandbox");
                    edgeOptions.addArguments("--disable-dev-shm-usage");
                }
                localDriver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                // Suppress the password manager / data-breach popup
                chromeOptions.setExperimentalOption("prefs", getPasswordManagerPrefs());
                chromeOptions.addArguments("--disable-features=PasswordLeakDetection");
                chromeOptions.addArguments("--incognito");
                if (isHeadless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                }
                localDriver = new ChromeDriver(chromeOptions);
                break;
        }

        // Store the newly created driver in the current thread's ThreadLocal storage
        setDriver(localDriver);

        // Window configuration and page-level timeout
        getDriver().manage().window().maximize();
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));

        // Navigate to the target application
        getDriver().get(BASE_URL);
    }

    // Disables Chrome/Edge password manager, credential saving and leak detection
    private Map<String, Object> getPasswordManagerPrefs() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        return prefs;
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            unloadDriver(); // Cleans up the thread to prevent memory leaks
        }
    }
}