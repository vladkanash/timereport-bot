package org.vladkanash.rendering.context;

import java.util.Map;
import java.util.Objects;

public class UserWeekWorklog {

    private String name;
    private String userId;
    private String avatarUrl;
    private String totalTime;
    private Map<String, String> submittedTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Map<String, String> getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(Map<String, String> submittedTime) {
        this.submittedTime = submittedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWeekWorklog that = (UserWeekWorklog) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(avatarUrl, that.avatarUrl) &&
                Objects.equals(totalTime, that.totalTime) &&
                Objects.equals(submittedTime, that.submittedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, userId, avatarUrl, totalTime, submittedTime);
    }
}
