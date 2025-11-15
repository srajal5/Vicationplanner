package com.vacationplanner.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading environment variables and configuration.
 */
public class EnvLoader {
    
    private static final Properties properties = new Properties();
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = EnvLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Could not load application.properties: " + e.getMessage());
        }
    }
    
    /**
     * Get a property value, checking environment variables first, then properties file.
     * 
     * @param key the property key
     * @param defaultValue the default value if not found
     * @return the property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        // Check environment variable first
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }
        
        // Check system property
        String sysValue = System.getProperty(key);
        if (sysValue != null && !sysValue.isEmpty()) {
            return sysValue;
        }
        
        // Check properties file
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get an environment variable or property value.
     * 
     * @param key the key to look up
     * @return the value or null if not found
     */
    public static String getEnv(String key) {
        return getProperty(key, null);
    }

    /**
     * Get an environment variable or property value with a default.
     * 
     * @param key the key to look up
     * @param defaultValue the default value if not found
     * @return the value or default value
     */
    public static String getEnv(String key, String defaultValue) {
        return getProperty(key, defaultValue);
    }
    
    /**
     * Get a property value, returning null if not found.
     * 
     * @param key the property key
     * @return the property value or null
     */
    public static String getProperty(String key) {
        return getProperty(key, null);
    }
    
    /**
     * Get an integer property value.
     * 
     * @param key the property key
     * @param defaultValue the default value if not found or invalid
     * @return the integer value or default value
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Invalid integer value for " + key + ": " + value);
            }
        }
        return defaultValue;
    }
    
    /**
     * Get a boolean property value.
     * 
     * @param key the property key
     * @param defaultValue the default value if not found
     * @return the boolean value or default value
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }
}
