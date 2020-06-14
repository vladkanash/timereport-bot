package org.vladkanash.util;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Config {

    private static final String LIST_DELIMITER = ",";
    private static final String PATH = "config.properties";

    private final Properties properties = new Properties();

    @Inject
    public Config() {
        try (var inputStream = Config.class.getClassLoader().getResourceAsStream(PATH)) {
            properties.load(Objects.requireNonNull(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public List<String> getList(String key) {
        return Arrays.asList(get(key).split(LIST_DELIMITER));
    }
}
