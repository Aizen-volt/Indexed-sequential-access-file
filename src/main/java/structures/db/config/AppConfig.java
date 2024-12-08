package main.java.structures.db.config;

import lombok.Getter;
import main.java.structures.db.model.Element;

import java.io.InputStream;
import java.util.Properties;

@Getter
public class AppConfig {

    private static final String DEFAULT_MAIN_FILE_PATH = "main.db";
    private static final String DEFAULT_INDEX_FILE_PATH = "index.db";
    private static final String DEFAULT_OVERFLOW_FILE_PATH = "index.db";
    private static final String DEFAULT_PAGE_BLOCK_FACTOR = "128";
    private static final String DEFAULT_ALPHA = "0.5";
    private static final String DEFAULT_BETA = "0.5";

    private final String mainFilePath;
    private final String indexFilePath;
    private final String overflowFilePath;

    private final int pageBlockFactor;

    private final double alpha;
    private final double beta;

    private AppConfig() {
        var properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new NoConfigFileLoadedException("Configuration file 'config.properties' not found in classpath");
            }

            properties.load(input);

            // Load properties
            mainFilePath = properties.getProperty("main.file.name", DEFAULT_MAIN_FILE_PATH);
            indexFilePath = properties.getProperty("index.file.name", DEFAULT_INDEX_FILE_PATH);
            overflowFilePath = properties.getProperty("overflow.file.name", DEFAULT_OVERFLOW_FILE_PATH);

            pageBlockFactor = Integer.parseInt(properties.getProperty("page.block.factor", DEFAULT_PAGE_BLOCK_FACTOR));

            alpha = Double.parseDouble(properties.getProperty("alpha", DEFAULT_ALPHA));
            beta = Double.parseDouble(properties.getProperty("beta", DEFAULT_BETA));

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
