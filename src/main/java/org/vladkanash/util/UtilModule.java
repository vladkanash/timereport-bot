package org.vladkanash.util;

import dagger.Module;
import dagger.Provides;

@Module
public interface UtilModule {

    String CONFIG_PATH = "config.properties";

    @Provides
    static Config getConfig() {
        return new Config(CONFIG_PATH);
    }
}
