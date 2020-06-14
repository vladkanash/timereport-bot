package org.vladkanash.rendering.context;

import java.util.Map;
import java.util.Objects;

public class UserWorklogData {

    private String name;
    private String userId;
    private String avatarUrl;
    private LoggedTimeData totalTime;
    private Map<String, LoggedTimeData> submittedTime;

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

    public LoggedTimeData getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(LoggedTimeData totalTime) {
        this.totalTime = totalTime;
    }

    public Map<String, LoggedTimeData> getSubmittedTime() {
        return submittedTime;
    }

    public void setSubmittedTime(Map<String, LoggedTimeData> submittedTime) {
        this.submittedTime = submittedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserWorklogData that = (UserWorklogData) o;
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
