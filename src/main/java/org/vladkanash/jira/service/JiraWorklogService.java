package org.vladkanash.jira.service;

import org.vladkanash.jira.entity.Worklog;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

public interface JiraWorklogService {

    Stream<Worklog> getUserWorklogs(Set<String> userIds, LocalDate startDate, LocalDate endDate);
}
