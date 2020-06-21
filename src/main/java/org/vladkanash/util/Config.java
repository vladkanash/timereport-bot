package org.vladkanash.util;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Singleton
public class Config {

    private final static String LIST_DELIMITER = ",";

    private final Properties properties = new Properties();

    @Inject
    public Config(String configPath) {
        try (var inputStream = Config.class.getClassLoader().getResourceAsStream(configPath)) {
            properties.load(Objects.requireNonNull(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        return properties.getProperty(key);
    }

    public List<String> getList(String key) {
        return List.of(get(key).split(LIST_DELIMITER));
    }
}
