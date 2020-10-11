package org.vladkanash.dao;

import com.google.gson.annotations.SerializedName;

public class User {

    private String name;

    @SerializedName("Jira ID")
    private String jiraId;

    @SerializedName("Slack ID")
    private String slackId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJiraId() {
        return jiraId;
    }

    public void setJiraId(String jiraId) {
        this.jiraId = jiraId;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", jiraId='" + jiraId + '\'' +
                ", slackId='" + slackId + '\'' +
                '}';
    }
}
