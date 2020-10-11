package org.vladkanash.config;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;
import org.cfg4j.source.system.EnvironmentVariablesConfigurationSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Singleton
public class Cfg4jConfig implements Config {

    private final ConfigurationProvider configurationProvider;

    @Inject
    public Cfg4jConfig(String configPath) {
        this.configurationProvider = getProvider(configPath);
    }

    @Override
    public String get(String key) {
        return configurationProvider.getProperty(key, String.class);
    }

    private ConfigurationProvider getProvider(String configPath) {
        var configSource = getConfigSource(configPath);
        var reloadStrategy = new PeriodicalReloadStrategy(5, TimeUnit.MINUTES);

        return new ConfigurationProviderBuilder()
                .withConfigurationSource(configSource)
                .withReloadStrategy(reloadStrategy)
                .build();
    }

    private ConfigurationSource getConfigSource(String configPath) {
        ConfigFilesProvider configFilesProvider = () -> Paths.get(configPath);
        var fileSource = new ClasspathConfigurationSource(configFilesProvider);
        var envSource = new EnvironmentVariablesConfigurationSource();

        return new MergeConfigurationSource(fileSource, envSource);
    }
}
