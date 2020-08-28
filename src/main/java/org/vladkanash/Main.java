package org.vladkanash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vladkanash.facade.SlackFacade;

import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

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
        var task = createTask(slackFacade);
        var period = Long.parseLong(context.getConfig().get("slack.reportRate"));
        new Timer(false).scheduleAtFixedRate(task, new Date(), period);
    }

    private static TimerTask createTask(SlackFacade slackFacade) {
        return new TimerTask() {
            @Override
            public void run() {
                LOG.info("Sending current week report...");
                slackFacade.sendCurrentWeekReport();
            }
        };
    }
}
