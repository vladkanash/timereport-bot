package org.vladkanash;

import dagger.Component;
import org.vladkanash.dao.DaoModule;
import org.vladkanash.facade.SlackFacade;
import org.vladkanash.jira.JiraModule;
import org.vladkanash.slack.SlackModule;
import org.vladkanash.util.Config;
import org.vladkanash.util.ConfigModule;

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
