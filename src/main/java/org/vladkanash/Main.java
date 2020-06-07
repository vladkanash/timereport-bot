package org.vladkanash;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.jira.util.RequestUtils;
import org.vladkanash.rendering.service.ImageRenderingService;
import org.vladkanash.slack.service.SlackService;
import org.vladkanash.util.TimeUtils;

import java.util.List;

import static org.vladkanash.util.Config.CONFIG;

public class Main {

    public static void main(String[] args) {
        var users = CONFIG.getList("jira.users.list");
        var rawQuery = CONFIG.get("jira.rest.worklog.query");
        var query = RequestUtils.createWorklogSearchQuery(rawQuery, users);

        var total = JiraWorklogService.getUserWorklogs(query)
                .filter(worklog -> isValidWorklog(worklog, users))
                .peek(System.out::println);

        var imageRenderingService = new ImageRenderingService();
        var image = imageRenderingService.renderWorklogReportImage(total);
        SlackService.uploadImage(image);
    }

    private static boolean isValidWorklog(Worklog worklog, List<String> users) {
        var isValidName = users.contains(worklog.getAuthor().getAccountId());
        var submitDate = worklog.getSubmissionDate();

        return TimeUtils.isInCurrentWeek(submitDate) && isValidName;
    }
}
