package org.vladkanash.jira.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.vladkanash.jira.entity.Issue;
import org.vladkanash.jira.entity.Worklog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vladkanash.jira.util.RequestUtils;
import org.vladkanash.util.TimeUtils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.vladkanash.util.Config.CONFIG;

public class JiraWorklogService {

    private static final String ISSUES = "issues";
    private static final Gson gson = new Gson();

    public static Stream<Worklog> getUserWorklogs(List<String> userIds, LocalDate startDate, LocalDate endDate) {
        var jqlQuery = RequestUtils.getWorklogSearchQuery(userIds, startDate, endDate);

        var users = CONFIG.getList("jira.users.list");
        return JiraRestApiService.searchQuery(jqlQuery)
                .stream()
                .flatMap(JiraWorklogService::parseResponse)
                .filter(worklog -> isValidWorklog(worklog, users, startDate, endDate));
    }

    private static Stream<Worklog> parseResponse(JSONObject response) {
        var issuesArray = response.getJSONArray(ISSUES);
        var partitionedIssues = parseIssues(issuesArray)
                .stream()
                .collect(Collectors.partitioningBy(JiraWorklogService::isDirectCallRequired));

        var worklogs = partitionedIssues.get(Boolean.FALSE)
                .stream()
                .map(Issue::getFields)
                .map(Issue.Fields::getWorklog)
                .flatMap(worklog -> worklog.getWorklogs().stream());

        var directCallWorklogs = partitionedIssues.get(Boolean.TRUE)
                .stream()
                .map(Issue::getCode)
                .flatMap(JiraWorklogService::getIssueWorklog);

        return Stream.concat(worklogs, directCallWorklogs);
    }

    private static List<Issue> parseIssues(JSONArray issuesArray) {
        Type issuesList = new TypeToken<ArrayList<Issue>>() {
        }.getType();
        return gson.fromJson(issuesArray.toString(), issuesList);
    }

    private static boolean isDirectCallRequired(Issue issue) {
        return issue.getFields().getWorklog().getMaxResults() <= issue.getFields().getWorklog().getWorklogs().size();
    }

    private static Stream<Worklog> getIssueWorklog(String issueCode) {
        return JiraRestApiService.getWorklogsForIssue(issueCode)
                .map(json -> gson.fromJson(json.toString(), Issue.Fields.Worklog.class))
                .map(Issue.Fields.Worklog::getWorklogs)
                .stream()
                .flatMap(Collection::stream);
    }

    private static boolean isValidWorklog(Worklog worklog, List<String> users,
                                          LocalDate startDate, LocalDate endDate) {

        var isValidName = users.contains(worklog.getAuthor().getAccountId());
        var submitDate = worklog.getSubmissionDate();

        var localDate = TimeUtils.toLocalDateTime(submitDate);

        return localDate.isBefore(endDate.plusDays(1).atStartOfDay())
                && localDate.isAfter(startDate.atStartOfDay())
                && isValidName;
    }
}
