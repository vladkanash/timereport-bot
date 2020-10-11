package org.vladkanash;

import dagger.Component;
import org.vladkanash.dao.DaoModule;
import org.vladkanash.facade.SlackFacade;
import org.vladkanash.util.Config;
import org.vladkanash.util.ConfigModule;

import javax.inject.Singleton;

@Component(modules = {ConfigModule.class, DaoModule.class})
@Singleton
public interface Context {

    Config getConfig();

    SlackFacade getSlackFacade();
}
