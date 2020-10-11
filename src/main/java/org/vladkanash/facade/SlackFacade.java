package org.vladkanash.facade;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.jetty.SlackAppServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vladkanash.config.Config;
import org.vladkanash.dao.User;
import org.vladkanash.dao.UserDao;
import org.vladkanash.slack.service.SlackService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Singleton
public class SlackFacade {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final TimeReportFacade timeReportFacade;
    private final SlackService slackService;
    private final Config config;
    private final UserDao userDao;

    @Inject
    public SlackFacade(TimeReportFacade timeReportFacade,
                       Config config,
                       SlackService slackService,
                       UserDao userDao) {
        this.timeReportFacade = timeReportFacade;
        this.slackService = slackService;
        this.config = config;
        this.userDao = userDao;
    }

    public void sendCurrentWeekReport() {
        var userIds = getUserJiraIds();
        var startDate = getCurrentDayOfWeekDate(1);
        var endDate = getCurrentDayOfWeekDate(7);

        timeReportFacade.getReport(userIds, startDate, endDate)
                .ifPresent(slackService::sendReport);
    }

    private Set<String> getUserJiraIds() {
        return userDao.getAllUsers().stream()
                .map(User::getJiraId)
                .collect(Collectors.toSet());
    }

    public void startSlackServer() throws Exception {
        var startDate = getCurrentDayOfWeekDate(1);
        var endDate = getCurrentDayOfWeekDate(7);

        var appConfig = getAppConfig();
        var app = new App(appConfig);

        app.command(config.get("slack.slashCommand.userReport.name"), (req, ctx) -> {
            executor.submit(() -> generateSlashResponse(startDate, endDate, ctx));
            return ctx.ack(config.get("slack.slashCommand.userReport.ack"));
        });

        var server = new SlackAppServer(app);
        server.start();
    }

    private LocalDate getCurrentDayOfWeekDate(int dayOfWeek) {
        LocalDate now = LocalDate.now();
        TemporalField tempField = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        return now.with(tempField, dayOfWeek);
    }

    private void generateSlashResponse(LocalDate startDate, LocalDate endDate, SlashCommandContext ctx) {
        LOG.info("Received slash command from user {}", ctx.getRequestUserId());
        getJiraUserId(ctx.getRequestUserId())
                .map(Set::of)
                .flatMap(userId -> timeReportFacade.getReport(userId, startDate, endDate))
                .map(slackService::generateReportMessage)
                .ifPresent(report -> slackService.postMessage(ctx.getResponseUrl(), report));
    }

    private Optional<String> getJiraUserId(String slackUserId) {
        return userDao.getUserBySlackId(slackUserId)
                .map(User::getJiraId);
    }

    private AppConfig getAppConfig() {
        return AppConfig.builder()
                .signingSecret(config.get("slack.auth.secret"))
                .build();
    }
}
