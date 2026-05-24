package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();

    static {

        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }
            PROPERTIES.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // ==========================================
    // GET STRING
    // ==========================================

    public static String get(String key) {

        return PROPERTIES.getProperty(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        String val = get(key);
        return (val == null || val.trim().isEmpty()) ? defaultValue : val.trim();
    }

    // ==========================================
    // GET BOOLEAN
    // ==========================================

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static boolean getBooleanOrDefault(String key, boolean defaultValue) {
        String val = get(key);
        return (val == null || val.trim().isEmpty()) ? defaultValue : Boolean.parseBoolean(val.trim());
    }

    // ==========================================
    // GET INT
    // ==========================================

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static int getIntOrDefault(String key, int defaultValue) {
        String val = get(key);
        try {
            return (val == null || val.trim().isEmpty()) ? defaultValue : Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}