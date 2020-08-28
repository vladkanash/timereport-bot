package org.vladkanash.jira.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.vladkanash.gson.adapter.LocalDateTimeAdapter;
import org.vladkanash.jira.entity.Issue;
import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.jira.entity.WorklogSearchResponse;
import org.vladkanash.util.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class JiraWorklogService {

    private final JiraRestApiService restApiService;
    private final Config config;
    private final Gson gson;

    @Inject
    public JiraWorklogService(JiraRestApiService restApiService, Config config) {
        this.restApiService = restApiService;
        this.config = config;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public Stream<Worklog> getUserWorklogs(Set<String> userIds, LocalDate startDate, LocalDate endDate) {
        var jqlQuery = getWorklogSearchQuery(userIds, startDate, endDate);
        return restApiService.searchQuery(jqlQuery)
                .stream()
                .flatMap(this::parseResponse)
                .filter(worklog -> isValidWorklog(worklog, userIds, startDate, endDate));
    }

    private String getWorklogSearchQuery(Set<String> userIds, LocalDate startDate, LocalDate endDate) {
        var rawQuery = config.get("jira.rest.worklogQuery");

        var isoStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        var isoEndDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        var escapedUsers = userIds
                .stream()
                .map(user -> "'" + user + "'")
                .collect(Collectors.joining(", "));

        return String.format(rawQuery, escapedUsers, isoStartDate, isoEndDate);
    }

    private Stream<Worklog> parseResponse(String response) {
        var partitionedIssues = parseIssues(response)
                .getIssues()
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

    private WorklogSearchResponse parseIssues(String searchResponse) {
        return gson.fromJson(searchResponse, WorklogSearchResponse.class);
    }

    private boolean isDirectCallRequired(Issue issue) {
        return issue.getFields().getWorklog().getMaxResults() <=
                issue.getFields().getWorklog().getWorklogs().size();
    }

    private Stream<Worklog> getIssueWorklog(String issueCode) {
        return restApiService.getWorklogsForIssue(issueCode)
                .map(json -> gson.fromJson(json, Issue.Fields.Worklog.class))
                .map(Issue.Fields.Worklog::getWorklogs)
                .stream()
                .flatMap(Collection::stream);
    }

    private boolean isValidWorklog(Worklog worklog, Set<String> users,
                                   LocalDate startDate, LocalDate endDate) {

        var isValidName = users.contains(worklog.getAuthor().getAccountId());
        var submitDate = worklog.getSubmissionDate();

        return submitDate.isBefore(endDate.plusDays(1).atStartOfDay())
                && submitDate.isAfter(startDate.atStartOfDay())
                && isValidName;
    }
}
