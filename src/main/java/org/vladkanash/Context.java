package org.vladkanash;

import dagger.Component;
import org.vladkanash.slack.facade.ReportGenerationFacade;
import org.vladkanash.slack.service.SlackService;
import org.vladkanash.util.Config;
import org.vladkanash.util.UtilModule;

import javax.inject.Singleton;

@Component(modules = {UtilModule.class})
@Singleton
public interface Context {

    ReportGenerationFacade getTimeReportFacade();

    SlackService getSlackService();

    Config getConfig();
}
