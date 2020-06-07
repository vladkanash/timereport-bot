package org.vladkanash.rendering.context;

import java.util.List;

public class WorklogContext {

    private List<UserWeekWorklog> userWorklogs;
    private List<String> weekDays;
    private String currentMonth;

    public List<UserWeekWorklog> getUserWorklogs() {
        return userWorklogs;
    }

    public void setUserWorklogs(List<UserWeekWorklog> userWorklogs) {
        this.userWorklogs = userWorklogs;
    }

    public List<String> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<String> weekDays) {
        this.weekDays = weekDays;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(String currentMonth) {
        this.currentMonth = currentMonth;
    }
}
