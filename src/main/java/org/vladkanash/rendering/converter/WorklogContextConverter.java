package org.vladkanash.rendering.converter;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.context.MonthData;
import org.vladkanash.rendering.context.UserWeekWorklog;
import org.vladkanash.rendering.context.WorklogContext;
import org.vladkanash.util.TimeUtils;

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

    public static WorklogContext convert(Stream<Worklog> worklogs) {
        var weekWorklog = new WorklogContext();
        var userWorklogs = getUserWorklogs(worklogs);
        weekWorklog.setWeekDays(getCurrentWeekDays());
        weekWorklog.setUserWorklogs(userWorklogs);
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

    private static List<UserWeekWorklog> getUserWorklogs(Stream<Worklog> worklogs) {
        return worklogs
                .collect(Collectors.groupingBy(w -> w.getAuthor().getAccountId()))
                .values()
                .stream()
                .map(WorklogContextConverter::getUserWeekWorklog)
                .collect(Collectors.toList());
    }

    private static UserWeekWorklog getUserWeekWorklog(List<Worklog> userWorklogs) {
        var result = new UserWeekWorklog();
        var worklog = userWorklogs.get(0);

        result.setUserId(worklog.getAuthor().getAccountId());
        result.setName(worklog.getAuthor().getDisplayName());
        result.setAvatarUrl(worklog.getAuthor().getAvatarUrls().getOrDefault(AVATAR_SIZE, ""));
        result.setSubmittedTime(getReportedSeconds(userWorklogs));
        result.setTotalTime(getTotalTime(userWorklogs));
        return result;
    }

    private static Map<String, String> getReportedSeconds(List<Worklog> userWorklogs) {
        return userWorklogs.stream()
                .collect(Collectors.groupingBy(work ->
                        TimeUtils.toLocalDate(work.getSubmissionDate())))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> TimeUtils.getDisplayDay(e.getKey()),
                        e -> getTotalTime(e.getValue())));
    }

    private static String getTotalTime(List<Worklog> userWorklogs) {
        var totalSeconds = userWorklogs.stream()
                .mapToInt(Worklog::getReportedSeconds)
                .sum();
        return String.valueOf(totalSeconds / 3600);
    }
}
