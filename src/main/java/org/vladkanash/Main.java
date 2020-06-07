package org.vladkanash;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.jira.service.JiraRestApiService;
import org.vladkanash.jira.service.JiraWorklogService;
import org.vladkanash.jira.util.RequestUtils;
import org.vladkanash.rendering.service.ImageRenderingService;
import org.vladkanash.util.TimeUtils;

import java.util.List;

import static org.vladkanash.util.Config.CONFIG;

public class Main {

    public static void main(String[] args) {
        var users = CONFIG.getList("jira.users.list");
        var rawQuery = CONFIG.get("jira.rest.worklog.query");
        var query = RequestUtils.createWorklogSearchQuery(rawQuery, users);

        var worklogService = new JiraWorklogService(createApiService());
        var total = worklogService.getUserWorklogs(query)
                .filter(worklog -> isValidWorklog(worklog, users))
                .peek(System.out::println);

        var imageRenderingService = new ImageRenderingService();
        imageRenderingService.renderWorklogReportImage(total);
    }

    private static JiraRestApiService createApiService() {
        return JiraRestApiService.builder()
                .token(CONFIG.get("jira.auth.token"))
                .username(CONFIG.get("jira.auth.user"))
                .serverUri(CONFIG.get("jira.server.uri"))
                .searchEndpoint(CONFIG.get("jira.rest.endpoint.search"))
                .worklogEndpoint(CONFIG.get("jira.rest.endpoint.worklog"))
                .build();
    }

    private static boolean isValidWorklog(Worklog worklog, List<String> users) {
        var isValidName = users.contains(worklog.getAuthor().getAccountId());
        var submitDate = worklog.getSubmissionDate();

        return TimeUtils.isInCurrentWeek(submitDate) && isValidName;
    }
}
