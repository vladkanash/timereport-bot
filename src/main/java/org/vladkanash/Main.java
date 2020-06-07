package org.vladkanash;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.jira.service.JiraRestApiService;
import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.jira.util.RequestUtils;
import org.vladkanash.rendering.service.ImageRenderingService;
import org.vladkanash.util.TimeUtils;
import org.vladkanash.util.Config;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        var users = Config.INSTANCE.getList("jira.users.list");
        var rawQuery = Config.INSTANCE.get("jira.rest.worklog.query");
        var query = RequestUtils.createWorklogSearchQuery(rawQuery, users);

        var worklogService = new JiraWorklogService(createApiService());
        var total = worklogService.getUserWorklogs(query)
                .filter(worklog -> isValidWorklog(worklog, users))
                .peek(System.out::println);

        var imageRenderingService = new ImageRenderingService();
        imageRenderingService.renderWorklogReportImage(total);
    }

    private static JiraRestApiService createApiService() {
        var config = Config.INSTANCE;

        return JiraRestApiService.builder()
                .token(config.get("jira.auth.token"))
                .username(config.get("jira.auth.user"))
                .serverUri(config.get("jira.server.uri"))
                .searchEndpoint(config.get("jira.rest.endpoint.search"))
                .worklogEndpoint(config.get("jira.rest.endpoint.worklog"))
                .build();
    }

    private static boolean isValidWorklog(Worklog worklog, List<String> users) {
        var isValidName = users.contains(worklog.getAuthor().getAccountId());
        var submitDate = worklog.getSubmissionDate();

        return TimeUtils.isInCurrentWeek(submitDate) && isValidName;
    }
}
