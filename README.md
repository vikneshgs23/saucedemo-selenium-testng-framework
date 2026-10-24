# SauceDemo E2E Test Automation Framework

An enterprise-grade, thread-safe test automation framework built using **Java**, **Selenium WebDriver**, **TestNG**, and **ExtentReports v5**, configured for parallel execution both locally and within **GitHub Actions CI/CD** pipelines.

---

## 🚀 Key Framework Features

- **Page Object Model (POM):** Clean decoupling between test scenarios, page element locators, and UI business workflows.
- **Thread-Safe Parallel Execution:** 
  - `ThreadLocal<WebDriver>` lifecycle management prevents driver cross-talk and memory leaks across parallel threads.
  - `ThreadLocal<ExtentTest>` ensures logging and screenshot attachments remain isolated per test thread.
- **Advanced Wait Strategies (`WaitUtils`):** Explicit wait wrappers with dynamic stale element reference recovery (`StaleElementReferenceException` retries) and URL change listeners.
- **Defect & Edge-Case Validation:** Automated verification of intentional visual bugs (`problem_user`), form-level field validations, and deep-link authorization barriers.
- **Multi-Browser & Headless Support:** Command-line switches for Chrome, Firefox, and Edge with pre-configured password-manager and leak-detection suppressions.
- **Rich HTML Reporting:** ExtentReports v5 output saved directly to `test-output/ExtentReport.html` with automated screenshot attachments on failure.

---

## 🛠️ Tech Stack & Prerequisites

- **Language:** Java 17+
- **Build Tool:** Apache Maven
- **Core Libraries:**
  - Selenium WebDriver `4.x`
  - TestNG `7.x`
  - ExtentReports `5.x`
- **Target Application:** [SauceDemo (Swag Labs)](https://www.saucedemo.com/)

---

## 📁 Project Structure

```text
├── .github/
│   └── workflows/
│       └── maven.yml                 # GitHub Actions CI workflow (Headless runner)
├── src/
│   ├── main/java/com/automation/
│   │   ├── base/
│   │   │   └── BaseTest.java         # ThreadLocal driver lifecycle, options & configurations
│   │   ├── pages/
│   │   │   ├── LoginPage.java        # Authentication & route authorization assertions
│   │   │   ├── InventoryPage.java    # Product catalog, burger menu, sorting & item asserts
│   │   │   ├── CartPage.java         # Cart modifications & badge validations
│   │   │   ├── CheckoutStepOne.java  # Form filling, input resets & validation banners
│   │   │   ├── CheckoutStepTwo.java  # Subtotal, tax calculations & pricing math
│   │   │   └── CheckoutComplete.java # Order confirmation verification
│   │   └── utils/
│   │       ├── ConfigReader.java     # Environment & credential configuration loader
│   │       ├── ExtentReportManager.java # ThreadLocal ExtentReports configuration
│   │       ├── ScreenshotUtil.java   # Failure screenshot capture utility
│   │       ├── TestListener.java     # TestNG listener managing reports and test failure hooks
│   │       └── WaitUtils.java        # Explicit wait wrappers & stale-safe extraction
│   └── test/java/com/automation/tests/
│       ├── LoginTest.java            # Standard and edge-case login scenarios
│       ├── CartTest.java             # Add/remove items and badge synchronization
│       ├── CheckoutTest.java         # Field validations and checkout steps
│       └── TC_EndtoEnd.java          # Core end-to-end regression test suite (TC_001 - TC_007)
├── test-output/
│   └── ExtentReport.html             # Generated HTML test execution dashboard
├── pom.xml                           # Dependencies & Maven Surefire Plugin configuration
├── testng.xml                        # Parallel test runner suite configuration
└── README.md                         # Documentation

🔄 CI/CD Pipeline (GitHub Actions)
The suite executes automatically on every push and pull_request targeting the main branch.

Key runner flags configured in BaseTest ensure seamless execution inside headless Linux virtual environments:

--headless=new

--no-sandbox

--disable-dev-shm-usage (prevents shared memory exhaustion on Linux containers)

--disable-features=PasswordLeakDetection & Incognito mode (suppresses automation-blocking browser alerts)

📊 Test Reports
After executing tests, view the detailed HTML execution report:

Plaintext
open test-output/ExtentReport.html
The report includes:

Test step status logs (Pass/Fail/Skip)

Execution timelines and durations per thread

Embedded base64 / path-linked failure screenshots captured via TestListener

⚡ Parallel Execution Architecture
Parallel execution is coordinated at the class level via testng.xml:

XML
<suite name="SauceDemo Test Suite" verbose="1" parallel="classes" thread-count="2">
    <listeners>
        <listener class-name="com.automation.utils.TestListener"/>
    </listeners>
    <test name="End-to-End Tests">
        <packages>
            <package name="com.automation.tests"/>
        </packages>
    </test>
</suite>
Thread-Safety Implementation: BaseTest stores driver sessions inside a ThreadLocal<WebDriver>, ensuring threads don't collide or step over browser sessions.

Reporting Thread-Safety: ExtentReportManager mirrors this pattern with ThreadLocal<ExtentTest>, isolating step entries and failure screenshots to the respective test thread.

Resource Sizing: Configured with thread-count="2" to maximize throughput while matching the standard 2 vCPUs available on GitHub Actions ubuntu-latest environments.
