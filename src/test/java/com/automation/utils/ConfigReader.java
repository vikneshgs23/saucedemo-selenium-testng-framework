package com.automation.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties file not found under src/test/resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage(), e);
        }
    }

    // Direct helper methods
    public static String getStandardUsername() {
        return properties.getProperty("standard.username");
    }

    public static String getStandardPassword() {
        return properties.getProperty("standard.password");
    }

    public static String getLockedUsername() {
        return properties.getProperty("locked.username");
    }

    public static String getProblemUsername() {
        return properties.getProperty("problem.username");
    }

    public static String getPerformanceUsername() {
        return properties.getProperty("performance.username");
    }

    // Generic fallback for any other property key
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}