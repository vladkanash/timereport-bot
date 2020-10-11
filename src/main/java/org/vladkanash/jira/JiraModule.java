package org.vladkanash.jira;

import dagger.Binds;
import dagger.Module;
import org.vladkanash.jira.service.DefaultJiraRestApiService;
import org.vladkanash.jira.service.DefaultJiraWorklogService;
import org.vladkanash.jira.service.JiraRestApiService;
import org.vladkanash.jira.service.JiraWorklogService;

@Module
public interface JiraModule {

    @Binds
    JiraRestApiService bindJiraRestApiService(DefaultJiraRestApiService jiraRestApiService);

    @Binds
    JiraWorklogService bindJiraWorklogService(DefaultJiraWorklogService jiraWorklogService);
}
