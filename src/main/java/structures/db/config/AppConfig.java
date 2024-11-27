package main.java.structures.db.config;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private AppConfig() {
        Properties properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new NoConfigLoadedException("Configuration file 'config.properties' not found in classpath");
            }

            properties.load(input);

            // Load properties

        } catch (Exception e) {
            throw new NoConfigLoadedException("Failed to load application configuration");
        }
    }

    private static final class InstanceHolder {
        private static final AppConfig instance = new AppConfig();
    }

    public static AppConfig getInstance() {
        return InstanceHolder.instance;
    }
}
