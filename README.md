# SauceDemo E2E Test Automation Framework

[![SauceDemo Test Automation CI](https://github.com/vikneshgs23/saucedemo-selenium-testng-framework/actions/workflows/regression.yml/badge.svg)](https://github.com/vikneshgs23/saucedemo-selenium-testng-framework/actions/workflows/regression.yml)

A thread-safe UI test automation framework for [SauceDemo (Swag Labs)](https://www.saucedemo.com/), built with **Java**, **Selenium WebDriver**, **TestNG** and **ExtentReports 5**. Tests run in parallel locally and in a **GitHub Actions** pipeline.

---

## Key Features

- **Page Object Model (POM):** Test scenarios, element locators and page workflows are kept separate.
- **Thread-safe parallel execution:**
  - `ThreadLocal<WebDriver>` gives each thread its own browser session, so parallel tests don't interfere with each other.
  - `ThreadLocal<ExtentTest>` keeps report logs and screenshots separate per test thread.
- **Explicit wait utilities (`WaitUtils`):** Wrappers for explicit waits, with retries on `StaleElementReferenceException` and URL-change waits.
- **Defect and edge-case checks:** Covers the intentional visual bugs of `problem_user`, form field validations and access blocking on direct URLs.
- **Multi-browser and headless support:** Chrome, Firefox and Edge, with browser options set to suppress password-manager and leak-detection popups.
- **HTML reporting:** ExtentReports output is written to `test-output/ExtentReport.html`, with a screenshot attached on failure.

---

## Tech Stack

- **Language:** Java 17+
- **Build tool:** Apache Maven
- **Libraries:** Selenium WebDriver 4.x, TestNG 7.x, ExtentReports 5.x
- **Application under test:** [SauceDemo](https://www.saucedemo.com/)

---

## Project Structure

```text
├── .github/
│   └── workflows/
│       └── regression.yml            # GitHub Actions CI workflow (headless)
├── src/
│   ├── main/java/com/automation/
│   │   ├── base/
│   │   │   └── BaseTest.java         # ThreadLocal driver setup, browser options and teardown
│   │   ├── pages/
│   │   │   ├── LoginPage.java        # Login and route-access checks
│   │   │   ├── InventoryPage.java    # Product list, menu, sorting and item checks
│   │   │   ├── CartPage.java         # Cart changes and badge checks
│   │   │   ├── CheckoutStepOne.java  # Shipping form, resets and validation messages
│   │   │   ├── CheckoutStepTwo.java  # Subtotal, tax and total calculations
│   │   │   └── CheckoutComplete.java # Order confirmation checks
│   │   └── utils/
│   │       ├── ConfigReader.java     # Loads environment and credential configuration
│   │       ├── ExtentReportManager.java # ThreadLocal ExtentReports setup
│   │       ├── ScreenshotUtil.java   # Captures screenshots on failure
│   │       ├── TestListener.java     # TestNG listener for reporting and failure hooks
│   │       └── WaitUtils.java        # Explicit waits and stale-element-safe helpers
│   └── test/java/com/automation/tests/
│       ├── LoginTest.java            # Valid, invalid and edge-case logins
│       ├── CartTest.java             # Adding and removing items, badge sync
│       ├── CheckoutTest.java         # Checkout field validations and steps
│       └── TC_EndtoEnd.java          # End-to-end scenarios (TC_001 - TC_007)
├── pom.xml                           # Dependencies and Maven Surefire configuration
├── testng.xml                        # Parallel suite configuration
└── README.md
```

`test-output/` is generated when tests run, so it is not part of the source tree.

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Apache Maven
- Chrome, Firefox or Edge installed

### Run the tests

```bash
git clone https://github.com/vikneshgs23/saucedemo-selenium-testng-framework.git
cd saucedemo-selenium-testng-framework
mvn clean test
```

---

## Parallel Execution

Parallel execution is configured at class level in `testng.xml`:

```xml
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
```

- **Driver isolation:** `BaseTest` stores each browser session in a `ThreadLocal<WebDriver>`, so threads never share a session.
- **Report isolation:** `ExtentReportManager` uses `ThreadLocal<ExtentTest>`, so each thread's steps and screenshots stay with its own test.
- **Thread count:** set to 2 to match the 2 vCPUs on GitHub Actions `ubuntu-latest` runners.

---

## Continuous Integration

The workflow in `.github/workflows/regression.yml` runs the suite on every push and pull request to `main`.

Browser options set in `BaseTest` let the tests run in a headless Linux environment:

- `--headless=new`
- `--no-sandbox`
- `--disable-dev-shm-usage` (avoids shared-memory problems in Linux containers)
- `--disable-features=PasswordLeakDetection` and incognito mode (avoids popups that block automation)

---

## Test Reports

After a run, open the HTML report:

```text
test-output/ExtentReport.html
```

The report includes:

- Pass, fail and skip status for each test
- Start time and duration for each test
- Failure screenshots captured by `TestListener`

---

## Author

**G S Viknesh** - [GitHub](https://github.com/vikneshgs23)
