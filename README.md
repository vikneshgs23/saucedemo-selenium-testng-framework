# SauceDemo E2E Test Automation Framework

[![Live Report](https://img.shields.io/badge/Extent%20Report-Live-blue)](https://vikneshgs23.github.io/saucedemo-selenium-testng-framework/)

A thread-safe UI test automation framework for [SauceDemo (Swag Labs)](https://www.saucedemo.com/), built with **Java**, **Selenium WebDriver**, **TestNG** and **ExtentReports 5**. Tests run in parallel locally and in a **GitHub Actions** pipeline, and the latest Extent report is published to GitHub Pages after every push to `main`.

---

## Key Features

- **Page Object Model (POM):** Test scenarios, element locators and page workflows are kept separate.
- **Thread-safe parallel execution:**
  - `ThreadLocal<WebDriver>` gives each thread its own browser session, so parallel tests don't interfere with each other.
  - `ThreadLocal<ExtentTest>` keeps report logs and screenshots separate per test thread.
- **Explicit wait utilities (`WaitUtils`):** Wrappers for explicit waits, with retries on `StaleElementReferenceException` and URL-change waits.
- **Defect and edge-case checks:** Covers the intentional bugs of `problem_user`, checkout form validations, boundary and special-character inputs, script-injection payloads, and access blocking on direct URLs.
- **Multi-browser and headless support:** Chrome, Firefox and Edge can be selected with `-Dbrowser`. Browser options suppress password-manager and leak-detection popups. CI runs headless Chrome.
- **HTML reporting:** ExtentReports output is written to `test-output/ExtentReport.html`. Failure screenshots are embedded in the report as Base64, so the report is a single self-contained file that also works on GitHub Pages.

---

## Tech Stack

- **Language:** Java 17+
- **Build tool:** Apache Maven
- **Libraries:** Selenium WebDriver 4.x, TestNG 7.x, ExtentReports 5.x
- **CI/CD:** GitHub Actions, GitHub Pages (live report)
- **Application under test:** [SauceDemo](https://www.saucedemo.com/)

---

## Project Structure

```text
├── .github/
│   └── workflows/
│       └── regression.yml               # CI workflow: headless run, artifacts, Pages deploy
├── src/
│   └── test/
│       ├── java/com/automation/
│       │   ├── base/
│       │   │   └── BaseTest.java        # ThreadLocal driver setup, browser options and teardown
│       │   ├── pages/
│       │   │   ├── LoginPage.java           # Login and route-access checks
│       │   │   ├── InventoryPage.java       # Product list, menu, sorting and item checks
│       │   │   ├── CartPage.java            # Cart changes and badge checks
│       │   │   ├── CheckoutStepOnePage.java # Shipping form, resets and validation messages
│       │   │   ├── CheckoutStepTwoPage.java # Subtotal, tax and total checks, order completion
│       │   │   └── CheckoutComplete.java    # Order confirmation checks
│       │   ├── utils/
│       │   │   ├── ConfigReader.java            # Loads environment and credential configuration
│       │   │   ├── ExtentReportManager.java     # ThreadLocal ExtentReports setup
│       │   │   ├── ScreenshotUtil.java          # Captures failure screenshots (saved and Base64-encoded)
│       │   │   ├── TestListener.java            # TestNG listener for reporting and failure hooks
│       │   │   └── WaitUtils.java               # Explicit waits and stale-element-safe helpers
│       │   └── tests/
│       │       ├── LoginTest.java           # Valid, invalid and edge-case logins
│       │       ├── CartTest.java            # Adding and removing items, badge sync
│       │       ├── CheckoutTest.java        # Checkout field validations and steps
│       │       └── TC_EndtoEnd.java         # End-to-end scenarios (TC_E2E_001 - TC_E2E_011)
│       └── resources/
│           ├── testng.xml               # Parallel suite configuration
│           └── config.properties        # Environment and credential configuration
├── pom.xml                              # Dependencies and Maven Surefire configuration
└── README.md
```

`test-output/` and `target/` are generated when tests run, so they are not part of the source tree.

---

## Test Coverage

`TC_EndtoEnd.java` contains the end-to-end scenarios below. Each test carries its ID in the TestNG `testName` and in the Extent report.

| Test ID | Scenario | Key Validations |
| :--- | :--- | :--- |
| **TC_E2E_001** | Multi-item purchase flow | Login, add two items, cart contents, checkout info, item subtotal ($39.98), total greater than subtotal (tax included), order confirmation message. |
| **TC_E2E_002** | Cart modification and state sync | Add three items, remove one, cart badge updates, removed item returns to "Add to cart" on the inventory page. |
| **TC_E2E_003** | Catalog sorting | Price (low to high), price (high to low) and name (Z to A) results compared against sorted copies of the lists. |
| **TC_E2E_004** | Checkout field validations | "First Name", "Last Name" and "Postal Code is required" errors on Checkout Step One. |
| **TC_E2E_005** | Checkout cancellation flows | Cancel on Step One returns to the cart with the item preserved. Cancel on the overview returns to the inventory with the badge preserved. |
| **TC_E2E_006** | `problem_user` known defects | All product images use the fallback image, and the Last Name field cannot be filled, so checkout is blocked with a validation error. |
| **TC_E2E_007** | Logout and session protection | Logout, browser Back button and direct `/inventory.html` access all land on the login page with the access-restriction banner. |
| **TC_E2E_008** | Empty cart checkout | Documents SauceDemo's current behaviour: Checkout Step One can be reached with 0 items in the cart. |
| **TC_E2E_009** | Unauthenticated direct URL access | Direct navigation to `/inventory.html` and `/checkout-step-one.html` redirects to login with the matching error banner. |
| **TC_E2E_010** | Boundary and special-character inputs | A 260-character first name, special characters in the last name and an alphanumeric postal code still reach the overview page. |
| **TC_E2E_011** | Script-injection payloads at checkout | `<script>` and `<img onerror>` payloads in checkout fields do not trigger a browser alert, and the flow continues to the overview page. |

Additional tests in `LoginTest`, `CartTest` and `CheckoutTest` cover login, cart and checkout behaviour at a smaller scope.

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

By default the tests run in a visible Chrome window. Use these options to change that:

```bash
# Headless Chrome (same as CI)
mvn clean test -Dheadless=true -Dbrowser=chrome

# Other browsers
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge

# A single test class
mvn test -Dtest=LoginTest
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
- **Thread count:** set to 2 to keep parallel runs stable on CI.

---

## Continuous Integration

The workflow in `.github/workflows/regression.yml` runs on every push and pull request to `main`. It:

1. Sets up Java 17 and runs `mvn clean test -Dheadless=true -Dbrowser=chrome`.
2. Uploads the Extent report as a build artifact (kept for 30 days), even when tests fail.
3. Uploads failure screenshots as a separate artifact when a run fails.
4. On pushes to `main` only (not pull requests), deploys the Extent report to GitHub Pages.

Browser options set in `BaseTest` let the tests run in a headless Linux environment:

- `--headless=new`
- `--no-sandbox`
- `--disable-dev-shm-usage` (avoids shared-memory problems in Linux containers)
- `--disable-features=PasswordLeakDetection` and incognito mode (avoids popups that block automation)

---

## Test Reports

**Live report:** [View the latest Extent report](https://vikneshgs23.github.io/saucedemo-selenium-testng-framework/) (published from the most recent push to `main`).

After a local run, open the HTML report:

```text
test-output/ExtentReport.html
```

The report includes:

- Pass, fail and skip status for each test
- Start time and duration for each test
- Failure screenshots, captured by `TestListener` and embedded in the report
- Environment details: application, operating system, Java version and browser

---

## Author

**Viknesh GS** - [GitHub](https://github.com/vikneshgs23) · [LinkedIn](https://www.linkedin.com/in/gs-viknesh)
