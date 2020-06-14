package org.vladkanash.jira.entity;

import java.util.List;

public class WorklogSearchResponse {

    private List<Issue> issues;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    @Override
    public String toString() {
        return "WorklogSearchResponse{" +
                "issues=" + issues +
                '}';
    }
}
