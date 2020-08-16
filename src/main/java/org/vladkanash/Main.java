package org.vladkanash;

public class Main {

    private static final Context context = DaggerContext.create();

    public static void main(String[] args) throws Exception {
        var slackFacade = context.getSlackFacade();

        slackFacade.sendCurrentWeekReport();
        slackFacade.startSlackServer();
    }
}
