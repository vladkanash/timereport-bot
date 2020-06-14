package org.vladkanash.jira.entity;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.Map;

public class Worklog {

    @SerializedName("started")
    private LocalDateTime submissionDate;

    @SerializedName("timeSpentSeconds")
    private Integer reportedSeconds;

    private Author author;

    public static class Author {

        private Map<String, String> avatarUrls;

        private String displayName;

        private String accountId;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public Map<String, String> getAvatarUrls() {
            return avatarUrls;
        }

        public void setAvatarUrls(Map<String, String> avatarUrls) {
            this.avatarUrls = avatarUrls;
        }

        @Override
        public String toString() {
            return "Author{" +
                    "displayName='" + displayName + '\'' +
                    ", accountId='" + accountId + '\'' +
                    '}';
        }
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Integer getReportedSeconds() {
        return reportedSeconds;
    }

    public void setReportedSeconds(Integer reportedSeconds) {
        this.reportedSeconds = reportedSeconds;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "WorklogSubmission{" +
                "submissionDate=" + submissionDate +
                ", reportedSeconds=" + reportedSeconds +
                ", author=" + author +
                '}';
    }
}
