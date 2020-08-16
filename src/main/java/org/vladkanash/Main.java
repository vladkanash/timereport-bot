package org.vladkanash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Context context = DaggerContext.create();

    public static void main(String[] args) throws Exception {
        var slackFacade = context.getSlackFacade();

        LOG.info("Sending current week report...");
        slackFacade.sendCurrentWeekReport();

        LOG.info("Starting slack server...");
        slackFacade.startSlackServer();
    }
}
