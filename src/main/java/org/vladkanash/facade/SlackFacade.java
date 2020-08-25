package org.vladkanash.facade;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.jetty.SlackAppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vladkanash.slack.service.SlackService;
import org.vladkanash.util.Config;
import org.vladkanash.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class SlackFacade {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TimeReportFacade timeReportFacade;
    private final SlackService slackService;
    private final Config config;

    @Inject
    public SlackFacade(TimeReportFacade timeReportFacade, Config config, SlackService slackService) {
        this.timeReportFacade = timeReportFacade;
        this.slackService = slackService;
        this.config = config;
    }

    public void sendCurrentWeekReport() {
        var userIds = new HashSet<>(config.getMap("users").values());
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

        app.command(config.get("slack.slash-command.user-report.name"), (req, ctx) -> {
            executor.submit(() -> generateSlashResponse(startDate, endDate, ctx));
            return ctx.ack(config.get("slack.slash-command.user-report.ack"));
        });

        var server = new SlackAppServer(app);
        server.start();
    }

    private void generateSlashResponse(LocalDate startDate, LocalDate endDate, SlashCommandContext ctx) {
        LOG.info("Received slash command from user {}", ctx.getRequestUserId());
        var jiraUserId = getJiraUserId(ctx.getRequestUserId());
        timeReportFacade.getReport(Set.of(jiraUserId), startDate, endDate)
                .map(slackService::generateReportMessage)
                .ifPresent(report -> slackService.postMessage(ctx.getResponseUrl(), report));
    }

    private String getJiraUserId(String slackUserId) {
        var userMap = config.getMap("users");
        return userMap.get(slackUserId);
    }

    private AppConfig getAppConfig() {
        return AppConfig.builder()
                .signingSecret(config.get("slack.auth.secret"))
                .build();
    }
}
