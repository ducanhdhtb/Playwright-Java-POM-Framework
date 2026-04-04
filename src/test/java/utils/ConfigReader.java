package utils;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    public static void init_prop() {
        if (properties != null) return;
        properties = new Properties();
        try (FileInputStream ip = new FileInputStream("./src/test/resources/config.properties")) {
            properties.load(ip);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
