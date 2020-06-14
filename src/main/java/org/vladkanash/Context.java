package org.vladkanash;

import dagger.Component;
import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.rendering.service.HtmlRenderingService;
import org.vladkanash.rendering.service.VelocityService;
import org.vladkanash.slack.service.SlackService;
import org.vladkanash.util.Config;

import javax.inject.Singleton;

@Component
@Singleton
public interface Context {

    JiraWorklogService getWorkLogService();

    VelocityService getVelocityService();

    HtmlRenderingService getHtmlRenderingService();

    SlackService getSlackService();

    Config getConfig();
}
