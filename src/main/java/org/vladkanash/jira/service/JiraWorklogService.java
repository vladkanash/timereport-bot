package org.vladkanash.jira.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.vladkanash.jira.entity.Issue;
import org.vladkanash.jira.entity.Worklog;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JiraWorklogService {

    private static final String ISSUES = "issues";
    private static final Gson gson = new Gson();

    public static Stream<Worklog> getUserWorklogs(String jqlQuery) {
        return JiraRestApiService.searchQuery(jqlQuery)
                .stream()
                .flatMap(JiraWorklogService::parseResponse);
    }

    private static Stream<Worklog> getIssueWorklog(String issueCode) {
        return JiraRestApiService.getWorklogsForIssue(issueCode)
                .map(json -> gson.fromJson(json.toString(), Issue.Fields.Worklog.class))
                .map(Issue.Fields.Worklog::getWorklogs)
                .stream()
                .flatMap(Collection::stream);
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
}
