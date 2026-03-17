package com.dmd.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger logger = LogManager.getLogger(ConfigLoader.class);
    private static Properties properties;
    private static final String CONFIG_PATH = "config/";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        String environment = System.getProperty("env", "dev");
        String propertiesFile = CONFIG_PATH + environment + ".properties";
        
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(propertiesFile)) {
            properties.load(fileInputStream);
            logger.info("Properties loaded successfully from: " + propertiesFile);
        } catch (IOException e) {
            logger.error("Failed to load properties file: " + propertiesFile, e);
            throw new RuntimeException("Unable to load properties file: " + propertiesFile, e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key: " + key + ", using default: " + defaultValue);
            return defaultValue;
        }
    }
}
