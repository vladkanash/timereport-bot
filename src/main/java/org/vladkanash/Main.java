package org.vladkanash;

import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.rendering.service.ImageRenderingService;
import org.vladkanash.rendering.service.VelocityRenderingService;
import org.vladkanash.slack.service.SlackService;
import org.vladkanash.util.TimeUtils;

import java.time.LocalDate;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.vladkanash.util.Config.CONFIG;

public class Main {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void main(String[] args) {
        var userIds = CONFIG.getList("jira.users.list");
        var startDate = TimeUtils.getCurrentDayOfWeekDate(1);
        var endDate = TimeUtils.getCurrentDayOfWeekDate(7);

        var worklogs = JiraWorklogService.getUserWorklogs(userIds, startDate, endDate)
                .peek(System.out::println);

        VelocityRenderingService.renderHtml(worklogs, startDate, endDate)
                .flatMap(ImageRenderingService::renderImage)
                .ifPresent(SlackService::sendReport);

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
//        var report = VelocityRenderingService.renderHtml(worklogs, getCurrentDayOfWeekDate(1), getCurrentDayOfWeekDate(7))
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
