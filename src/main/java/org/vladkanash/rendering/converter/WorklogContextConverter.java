package org.vladkanash.rendering.converter;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.context.LoggedTimeData;
import org.vladkanash.rendering.context.MonthData;
import org.vladkanash.rendering.context.UserWorklogData;
import org.vladkanash.rendering.context.WorklogSummary;
import org.vladkanash.util.TimeUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorklogContextConverter {

    public static final String AVATAR_SIZE = "48x48";
    public static final int DAYS_IN_WEEK = 7;

    public static WorklogSummary convert(Stream<Worklog> worklogs) {
        var weekWorklog = new WorklogSummary();
        var userWorklogs = getUserWorklogs(worklogs);
        weekWorklog.setDayNames(getCurrentWeekDays());
        weekWorklog.setUserWorklogData(userWorklogs);
        weekWorklog.setMonthData(getMonthData());
        return weekWorklog;
    }

    private static List<MonthData> getMonthData() {
        return IntStream.rangeClosed(1, DAYS_IN_WEEK)
                .mapToObj(TimeUtils::getCurrentDayOfWeekDate)
                .map(TimeUtils::getDisplayMonth)
                .collect(Collectors.toMap(Function.identity(), m -> 1, Integer::sum, LinkedHashMap::new))
                .entrySet()
                .stream()
                .map(WorklogContextConverter::createMonthData)
                .collect(Collectors.toList());
    }

    private static MonthData createMonthData(Map.Entry<String, Integer> entry) {
        var month = new MonthData();
        month.setName(entry.getKey());
        month.setDaysCount(entry.getValue());
        return month;
    }

    private static List<String> getCurrentWeekDays() {
        return IntStream.rangeClosed(1, DAYS_IN_WEEK)
                .mapToObj(TimeUtils::getCurrentDayOfWeekDate)
                .map(TimeUtils::getDisplayDay)
                .collect(Collectors.toList());
    }

    private static List<UserWorklogData> getUserWorklogs(Stream<Worklog> worklogs) {
        return worklogs
                .collect(Collectors.groupingBy(w -> w.getAuthor().getAccountId()))
                .values()
                .stream()
                .map(WorklogContextConverter::getUserWeekWorklog)
                .collect(Collectors.toList());
    }

    private static UserWorklogData getUserWeekWorklog(List<Worklog> userWorklogs) {
        var result = new UserWorklogData();
        var worklog = userWorklogs.get(0);

        result.setUserId(worklog.getAuthor().getAccountId());
        result.setName(worklog.getAuthor().getDisplayName());
        result.setAvatarUrl(worklog.getAuthor().getAvatarUrls().getOrDefault(AVATAR_SIZE, ""));
        result.setSubmittedTime(getReportedTime(userWorklogs));
        result.setTotalTime(getTotalTime(userWorklogs));
        return result;
    }

    private static Map<String, LoggedTimeData> getReportedTime(List<Worklog> userWorklogs) {
        var reportedTime = userWorklogs.stream()
                .collect(Collectors.groupingBy(work ->
                        TimeUtils.toLocalDate(work.getSubmissionDate())));

        return TimeUtils.getCurrentDayOfWeekDate(1)
                .datesUntil(LocalDate.now().plusDays(1))
                .collect(Collectors.toMap(TimeUtils::getDisplayDay,
                        date -> getTotalTime(reportedTime.getOrDefault(date, Collections.emptyList()))));
    }

    private static LoggedTimeData getTotalTime(List<Worklog> userWorklogs) {
        var totalSeconds = userWorklogs.stream()
                .mapToInt(Worklog::getReportedSeconds)
                .sum();

        var totalMinutes = totalSeconds / 60;
        var hoursPart = totalMinutes / 60;
        var minutesPart = totalMinutes % 60;

        var result = new LoggedTimeData();
        result.setHours(String.valueOf(hoursPart));
        result.setMinutes(minutesPart != 0 ? String.valueOf(minutesPart) : "");
        return result;
    }
}
