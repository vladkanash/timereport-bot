package org.vladkanash;

import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vladkanash.facade.SlackFacade;

import java.lang.invoke.MethodHandles;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Context context = DaggerContext.create();

    public static void main(String[] args) throws Exception {
        final var slackFacade = context.getSlackFacade();

        scheduleReportTask(slackFacade);

        LOG.info("Starting slack server...");
        slackFacade.startSlackServer();
    }

    private static void scheduleReportTask(SlackFacade slackFacade) {
        var scheduler = new Scheduler();
        scheduler.setDaemon(false);
        var cronExpression = context.getConfig().get("slack.reportSchedule");
        scheduler.schedule(cronExpression, () -> sendReport(slackFacade));
        scheduler.start();
    }

    private static void sendReport(SlackFacade slackFacade) {
        LOG.info("Sending current week report...");
        slackFacade.sendCurrentWeekReport();
    }
}
