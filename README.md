# SauceDemo (Swag Labs) Test Automation Framework

An industry-standard test automation framework for [SauceDemo](https://www.saucedemo.com/) built using **Java**, **Selenium WebDriver**, and **TestNG**, implementing the **Page Object Model (POM)** design pattern.

---

## Tech Stack & Architecture

- **Language:** Java 17+
- **Core Library:** Selenium WebDriver 4
- **Test Runner:** TestNG
- **Build Tool:** Apache Maven
- **Design Pattern:** Page Object Model (POM)
- **CI/CD:** GitHub Actions (Ubuntu / Xvfb Headless Integration)

---

## Project Structure
```text
src/test
├── java
│   └── com.automation
│       ├── base       # WebDriver setup, cross-browser initialization, teardown
│       ├── pages      # Page Object Model classes (locators & actions)
│       ├── tests      # Functional TestNG test cases
│       └── utils      # Explicit wait helpers, listeners, config utilities
└── resources
    ├── testng.xml     # Test suite execution mapping
    └── config.properties # Environment-level variables
```
---

## Local Setup & Execution

### Prerequisites
- JDK 17 or higher
- Apache Maven 3.8+
- Google Chrome browser

### Run Tests via Maven
bash

Execute entire suite mapped via testng.xml
mvn clean test

Execute a single test class
mvn test -Dtest=LoginTest