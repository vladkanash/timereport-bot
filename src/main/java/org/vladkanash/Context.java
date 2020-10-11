package org.vladkanash;

import dagger.Component;
import org.vladkanash.config.Config;
import org.vladkanash.config.ConfigModule;
import org.vladkanash.dao.DaoModule;
import org.vladkanash.facade.SlackFacade;
import org.vladkanash.jira.JiraModule;
import org.vladkanash.slack.SlackModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        ConfigModule.class,
        DaoModule.class,
        JiraModule.class,
        SlackModule.class
})
public interface Context {

    Config getConfig();

    SlackFacade getSlackFacade();
}
