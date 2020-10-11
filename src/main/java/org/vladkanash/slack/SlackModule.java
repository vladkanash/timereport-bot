package org.vladkanash.slack;

import dagger.Binds;
import dagger.Module;
import org.vladkanash.slack.service.DefaultSlackService;
import org.vladkanash.slack.service.SlackService;

@Module
public interface SlackModule {

    @Binds
    SlackService bindSlackService(DefaultSlackService defaultSlackService);
}
