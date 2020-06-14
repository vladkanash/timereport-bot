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
import java.util.stream.Stream;

public class WorklogContextConverter {

    public static final String AVATAR_SIZE = "48x48";

    public static WorklogSummary convert(Stream<Worklog> worklogs, LocalDate startDate, LocalDate endDate) {
        var worklogList = worklogs.collect(Collectors.toList());

        var weekWorklog = new WorklogSummary();
        var userWorklogs = getUserWorklogs(worklogList, startDate);
        weekWorklog.setDayNames(getDayNames(startDate, endDate));
        weekWorklog.setUserWorklogData(userWorklogs);
        weekWorklog.setMonthData(getMonthData(startDate, endDate));
        return weekWorklog;
    }

    private static List<MonthData> getMonthData(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
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

    private static List<String> getDayNames(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .map(TimeUtils::getDisplayDay)
                .collect(Collectors.toList());
    }

    private static List<UserWorklogData> getUserWorklogs(List<Worklog> worklogs, LocalDate startDate) {
        return worklogs
                .stream()
                .collect(Collectors.groupingBy(w -> w.getAuthor().getAccountId()))
                .values()
                .stream()
                .map(UserWs -> getUserWorklog(UserWs, startDate))
                .collect(Collectors.toList());
    }

    private static UserWorklogData getUserWorklog(List<Worklog> userWorklogs, LocalDate startDate) {
        var result = new UserWorklogData();
        var worklog = userWorklogs.get(0);

        result.setUserId(worklog.getAuthor().getAccountId());
        result.setName(worklog.getAuthor().getDisplayName());
        result.setAvatarUrl(worklog.getAuthor().getAvatarUrls().getOrDefault(AVATAR_SIZE, ""));
        result.setSubmittedTime(getSubmittedTime(userWorklogs, startDate));
        result.setTotalTime(getTotalTime(userWorklogs));
        return result;
    }

    private static Map<String, LoggedTimeData> getSubmittedTime(List<Worklog> userWorklogs, LocalDate startDate) {
        var reportedTime = userWorklogs.stream()
                .collect(Collectors.groupingBy(log -> log.getSubmissionDate().toLocalDate()));

        return startDate.datesUntil(LocalDate.now().plusDays(1))
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
