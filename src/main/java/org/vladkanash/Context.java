package org.vladkanash;

import dagger.Component;
import org.vladkanash.facade.SlackFacade;
import org.vladkanash.util.Config;
import org.vladkanash.util.UtilModule;

import javax.inject.Singleton;

@Component(modules = {UtilModule.class})
@Singleton
public interface Context {

    Config getConfig();

    SlackFacade getSlackFacade();
}
