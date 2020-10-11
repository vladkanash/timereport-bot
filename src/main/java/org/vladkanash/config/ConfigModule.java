package org.vladkanash.config;

import dagger.Module;
import dagger.Provides;

@Module
public interface ConfigModule {

    String CONFIG_PATH = "config.yml";

    @Provides
    static Config getConfig() {
        return new Cfg4jConfig(CONFIG_PATH);
    }
}
