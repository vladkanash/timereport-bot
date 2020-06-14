package org.vladkanash.rendering.context;

import java.util.List;

public class WorklogSummary {

    private List<UserWorklogData> userWorklogData;
    private List<String> dayNames;
    private List<MonthData> monthData;

    public List<UserWorklogData> getUserWorklogData() {
        return userWorklogData;
    }

    public void setUserWorklogData(List<UserWorklogData> userWorklogData) {
        this.userWorklogData = userWorklogData;
    }

    public List<String> getDayNames() {
        return dayNames;
    }

    public void setDayNames(List<String> dayNames) {
        this.dayNames = dayNames;
    }

    public List<MonthData> getMonthData() {
        return monthData;
    }

    public void setMonthData(List<MonthData> monthData) {
        this.monthData = monthData;
    }
}
