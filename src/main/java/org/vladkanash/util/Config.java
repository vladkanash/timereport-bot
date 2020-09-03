package org.vladkanash.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class Config {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String PATH_SEPARATOR = "\\.";

    private final Map<String, Object> config = new HashMap<>();

    @Inject
    public Config(String configPath) {
        var yaml = new Yaml();
        try (var inputStream = Config.class.getClassLoader().getResourceAsStream(configPath)) {
            this.config.putAll(yaml.load(inputStream));
        } catch (Exception e) {
            LOG.error("An error occurred while trying to load config", e);
            throw new IllegalStateException(e);
        }
    }

    public String get(String key) {
        Object result = getLowestLevelObject(key);
        if (!(result instanceof String)) {
            throw new IllegalArgumentException("No String value exists for such key: " + key);
        }
        return (String) result;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getMap(String key) {
        Object result = getLowestLevelObject(key);
        if (!(result instanceof Map)) {
            throw new IllegalArgumentException("No Map<String, String> value exists for such key: " + key);
        }
        return (Map<String, String>) result;
    }

    @SuppressWarnings("unchecked")
    private Object getLowestLevelObject(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }

        var keys = key.split(PATH_SEPARATOR);
        var result = this.config;

        for (int i = 0; i < keys.length - 1; i++) {
            String k = keys[i];
            var newResult = result.get(k);
            if (newResult instanceof Map) {
                result = (Map<String, Object>) newResult;
            } else {
                throw new IllegalArgumentException("No value exists for such key: " + key);
            }
        }

        return result.get(keys[keys.length - 1]);
    }
}
