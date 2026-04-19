package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class ConfigReader {

    private static final String DEFAULT_CONFIG_FILE = "config.properties";
    private static final String ACTIVE_ENV = resolveEnvironment();
    private static final Properties properties = loadProperties();

    private ConfigReader() {
    }

    private static Properties loadProperties() {
        Properties loaded = new Properties();

        loadFromResource(loaded, DEFAULT_CONFIG_FILE);
        loadFromResource(loaded, "config-" + ACTIVE_ENV + ".properties");

        return loaded;
    }

    private static void loadFromResource(Properties target, String resourceName) {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                target.load(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read configuration resource: " + resourceName, e);
        }
    }

    private static String resolveEnvironment() {
        String value = System.getProperty("env");
        if (value == null || value.isBlank()) {
            value = System.getenv("ENV");
        }
        if (value == null || value.isBlank()) {
            return "dev";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public static String getActiveEnvironment() {
        return ACTIVE_ENV;
    }

    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String environmentProperty = System.getenv(key.replace('.', '_').toUpperCase(Locale.ROOT));
        if (environmentProperty != null && !environmentProperty.isBlank()) {
            return environmentProperty;
        }

        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }
}
