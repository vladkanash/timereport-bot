package org.vladkanash.facade;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.jetty.SlackAppServer;
import org.vladkanash.slack.service.SlackService;
import org.vladkanash.util.Config;
import org.vladkanash.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class SlackFacade {

    private final TimeReportFacade timeReportFacade;
    private final SlackService slackService;
    private final Config config;

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public SlackFacade(TimeReportFacade timeReportFacade, Config config, SlackService slackService) {
        this.timeReportFacade = timeReportFacade;
        this.slackService = slackService;
        this.config = config;
    }

    public void sendCurrentWeekReport() {
        var userIds = config.getList("jira.users.list");
        var startDate = TimeUtils.getCurrentDayOfWeekDate(1);
        var endDate = TimeUtils.getCurrentDayOfWeekDate(7);

        timeReportFacade.getReport(userIds, startDate, endDate)
                .ifPresent(slackService::sendReport);
    }

    public void startSlackServer() throws Exception {
        var startDate = TimeUtils.getCurrentDayOfWeekDate(1);
        var endDate = TimeUtils.getCurrentDayOfWeekDate(7);

        var appConfig = getAppConfig();
        var app = new App(appConfig);

        app.command(config.get("slack.slash.command.user.report"), (req, ctx) -> {
            executor.submit(() -> generateSlashResponse(startDate, endDate, ctx));
            return ctx.ack(config.get("slack.slash.command.ack"));
        });

        var server = new SlackAppServer(app, 4390);
        server.start();
    }

    private void generateSlashResponse(LocalDate startDate, LocalDate endDate, SlashCommandContext ctx) {
        var jiraUserId = getJiraUserId(ctx.getRequestUserId());
        timeReportFacade.getReport(List.of(jiraUserId), startDate, endDate)
                .map(slackService::generateReportMessage)
                .ifPresent(report -> slackService.postMessage(ctx.getResponseUrl(), report));
    }

    private String getJiraUserId(String slackUserId) {
        var userMap = Map.of("slackUserId", "jiraUserId");
        return userMap.get(slackUserId);
    }

    private AppConfig getAppConfig() {
        return AppConfig.builder()
                .signingSecret(config.get("slack.auth.secret"))
                .build();
    }
}
