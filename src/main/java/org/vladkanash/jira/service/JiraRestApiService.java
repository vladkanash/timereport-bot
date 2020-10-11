package org.vladkanash.jira.service;

import java.util.Optional;

public interface JiraRestApiService {

    Optional<String> getWorklogsForIssue(String issueCode);

    Optional<String> searchQuery(String query);
}
