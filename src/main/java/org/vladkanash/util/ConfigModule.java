package org.vladkanash.util;

import dagger.Module;
import dagger.Provides;

@Module
public interface ConfigModule {

    String CONFIG_PATH = "config.yml";

    @Provides
    static Config getConfig() {
        return new Config(CONFIG_PATH);
    }
}
