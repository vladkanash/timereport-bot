package org.vladkanash.rendering.context;

import java.util.List;

public class WorklogContext {

    private List<UserWeekWorklog> userWorklogs;
    private List<String> weekDays;
    private List<MonthData> monthData;

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

    public List<MonthData> getMonthData() {
        return monthData;
    }

    public void setMonthData(List<MonthData> monthData) {
        this.monthData = monthData;
    }
}
