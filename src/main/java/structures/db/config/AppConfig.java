package main.java.structures.db.config;

import lombok.Getter;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final String DEFAULT_PAGE_BLOCK_FACTOR = "128";

    @Getter
    private final int pageBlockFactor;

    private AppConfig() {
        var properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new NoConfigFileLoadedException("Configuration file 'config.properties' not found in classpath");
            }

            properties.load(input);

            // Load properties
            pageBlockFactor = Integer.parseInt(properties.getProperty("page.block.factor", DEFAULT_PAGE_BLOCK_FACTOR));

        } catch (Exception e) {
            throw new NoConfigFileLoadedException("Failed to load application configuration");
        }
    }

    private static final class InstanceHolder {
        private static final AppConfig instance = new AppConfig();
    }

    public static AppConfig getInstance() {
        return InstanceHolder.instance;
    }
}