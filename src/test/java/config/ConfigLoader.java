package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties PROPERTIES = new Properties();

    static {

        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream(
                        "config.properties")) {

            if (input == null) {

                throw new RuntimeException(
                        "config.properties not found");
            }

            PROPERTIES.load(input);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to load config.properties",
                    e);
        }
    }

    // ==========================================
    // GET STRING
    // ==========================================

    public static String get(
            String key) {

        return PROPERTIES.getProperty(key);
    }

    // ==========================================
    // GET BOOLEAN
    // ==========================================

    public static boolean getBoolean(
            String key) {

        return Boolean.parseBoolean(
                get(key));
    }

    // ==========================================
    // GET INT
    // ==========================================

    public static int getInt(
            String key) {

        return Integer.parseInt(
                get(key));
    }
}