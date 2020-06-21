package org.vladkanash;

import org.vladkanash.util.TimeUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final Context context = DaggerContext.create();

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        var slackService = context.getSlackService();
        var timeReportFacade = context.getTimeReportFacade();
        var config = context.getConfig();

        var userIds = config.getList("jira.users.list");
        var startDate = TimeUtils.getCurrentDayOfWeekDate(1);
        var endDate = TimeUtils.getCurrentDayOfWeekDate(7);

        timeReportFacade.getReport(userIds, startDate, endDate)
                .ifPresent(image -> {
                    var webhookUrl = config.get("slack.url.webhook");
                    var report = slackService.generateReport(image);
                    slackService.postMessage(webhookUrl, report);
                });

//        var app = new App();
//        app.command("/report", (req, ctx) -> {
//            executor.submit(() -> sendReport(ctx.getResponseUrl()));
//            return ctx.ack("Your report is being generated...");
//        });
//
//        SlackAppServer server = new SlackAppServer(app, 4390);
//        server.start();
    }

//    private static void sendReport(String url) {
//        var query = RequestUtils.getWorklogSearchQuery();
//
//        var worklogs = JiraWorklogService.getUserWorklogs(query)
//                .peek(System.out::println);
//
//        var report = VelocityRenderingService.renderHtml(worklogs, getCurrentDayOfWeekDate(1),
//        getCurrentDayOfWeekDate(7))
//                .flatMap(ImageRenderingService::renderImage)
//                .map(SlackService::generateReport)
//                .orElse(null);
//
//        try {
//            SlackService.postMessage(url, report);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String generateReport() {
//        var query = RequestUtils.getWorklogSearchQuery();
//
//        var worklogs = JiraWorklogService.getUserWorklogs(query)
//                .peek(System.out::println);
//
//        return VelocityRenderingService.renderHtml(worklogs, getCurrentDayOfWeekDate(1), getCurrentDayOfWeekDate(7))
//                .flatMap(ImageRenderingService::renderImage)
//                .map(SlackService::generateReport)
//                .orElse(null);
//    }
}
