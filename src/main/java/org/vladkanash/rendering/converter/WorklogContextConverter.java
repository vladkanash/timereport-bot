package org.vladkanash.rendering.converter;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.context.UserWeekWorklog;
import org.vladkanash.rendering.context.WorklogContext;
import org.vladkanash.util.TimeUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorklogContextConverter {

    public static final String AVATAR_SIZE = "48x48";
    public static final String HOUR = "h";

    public static WorklogContext convert(Stream<Worklog> worklogs) {
        var weekWorklog = new WorklogContext();
        var userWorklogs = getUserWorklogs(worklogs);
        weekWorklog.setWeekDays(getCurrentWeekDays());
        weekWorklog.setUserWorklogs(userWorklogs);
        weekWorklog.setCurrentMonth(getCurrentMonth());
        return weekWorklog;
    }

    private static String getCurrentMonth() {
        return TimeUtils.getDisplayMonth(LocalDate.now());
    }

    private static List<String> getCurrentWeekDays() {
        return IntStream.rangeClosed(1, 7)
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
        return (totalSeconds / 3600) + HOUR;
    }
}
