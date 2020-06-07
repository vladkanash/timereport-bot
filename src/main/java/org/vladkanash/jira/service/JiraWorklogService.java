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

    private final Gson gson = new Gson();
    private final JiraRestApiService restService;

    public JiraWorklogService(JiraRestApiService restService) {
        this.restService = restService;
    }

    public Stream<Worklog> getUserWorklogs(String jqlQuery) {
        return restService.searchQuery(jqlQuery)
                .stream()
                .flatMap(this::parseResponse);
    }

    private Stream<Worklog> getIssueWorklog(String issueCode) {
        return restService.getWorklogsForIssue(issueCode)
                .map(json -> gson.fromJson(json.toString(), Issue.Fields.Worklog.class))
                .map(Issue.Fields.Worklog::getWorklogs)
                .stream()
                .flatMap(Collection::stream);
    }

    private Stream<Worklog> parseResponse(JSONObject response) {
        var issuesArray = response.getJSONArray(ISSUES);
        var partitionedIssues = parseIssues(issuesArray)
                .stream()
                .collect(Collectors.partitioningBy(this::isDirectCallRequired));

        var worklogs = partitionedIssues.get(Boolean.FALSE)
                .stream()
                .map(Issue::getFields)
                .map(Issue.Fields::getWorklog)
                .flatMap(worklog -> worklog.getWorklogs().stream());

        var directCallWorklogs = partitionedIssues.get(Boolean.TRUE)
                .stream()
                .map(Issue::getCode)
                .flatMap(this::getIssueWorklog);

        return Stream.concat(worklogs, directCallWorklogs);
    }

    private List<Issue> parseIssues(JSONArray issuesArray) {
        Type issuesList = new TypeToken<ArrayList<Issue>>() {
        }.getType();
        return gson.fromJson(issuesArray.toString(), issuesList);
    }

    private boolean isDirectCallRequired(Issue issue) {
        return issue.getFields().getWorklog().getMaxResults() <= issue.getFields().getWorklog().getWorklogs().size();
    }
}
