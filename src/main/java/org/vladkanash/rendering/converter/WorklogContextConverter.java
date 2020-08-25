package org.vladkanash.rendering.converter;

import org.vladkanash.jira.entity.Worklog;
import org.vladkanash.rendering.context.LoggedTimeData;
import org.vladkanash.rendering.context.MonthData;
import org.vladkanash.rendering.context.UserWorklogData;
import org.vladkanash.rendering.context.WorklogSummary;
import org.vladkanash.util.TimeUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class WorklogContextConverter {

    @Inject
    public WorklogContextConverter() {
    }

    public static final String AVATAR_SIZE = "48x48";

    public WorklogSummary convert(Stream<Worklog> worklogs, LocalDate startDate, LocalDate endDate) {
        Supplier<Stream<LocalDate>> dates = () -> startDate.datesUntil(endDate.plusDays(1));

        var weekWorklog = new WorklogSummary();
        var userWorklogs = getUserWorklogs(worklogs, dates);
        weekWorklog.setDayNames(getDayNames(dates.get()));
        weekWorklog.setUserWorklogData(userWorklogs);
        weekWorklog.setMonthData(getMonthData(dates.get()));
        return weekWorklog;
    }

    private List<MonthData> getMonthData(Stream<LocalDate> dates) {
        return dates
                .map(TimeUtils::getDisplayMonth)
                .collect(Collectors.toMap(Function.identity(), m -> 1, Integer::sum, LinkedHashMap::new))
                .entrySet()
                .stream()
                .map(this::createMonthData)
                .collect(Collectors.toList());
    }

    private MonthData createMonthData(Map.Entry<String, Integer> entry) {
        var month = new MonthData();
        month.setName(entry.getKey());
        month.setDaysCount(entry.getValue());
        return month;
    }

    private List<String> getDayNames(Stream<LocalDate> dates) {
        return dates
                .map(TimeUtils::getDisplayDay)
                .collect(Collectors.toList());
    }

    private List<UserWorklogData> getUserWorklogs(Stream<Worklog> worklogs, Supplier<Stream<LocalDate>> dates) {
        return worklogs
                .collect(Collectors.groupingBy(w -> w.getAuthor().getAccountId()))
                .values()
                .stream()
                .map(UserWs -> getUserWorklog(UserWs, dates))
                .collect(Collectors.toList());
    }

    private UserWorklogData getUserWorklog(List<Worklog> userWorklogs, Supplier<Stream<LocalDate>> dates) {
        var result = new UserWorklogData();
        var worklog = userWorklogs.get(0);

        result.setUserId(worklog.getAuthor().getAccountId());
        result.setName(worklog.getAuthor().getDisplayName());
        result.setAvatarUrl(worklog.getAuthor().getAvatarUrls().getOrDefault(AVATAR_SIZE, ""));
        result.setSubmittedTime(getSubmittedTime(userWorklogs, dates.get()));
        result.setTotalTime(getTotalTime(userWorklogs));
        return result;
    }

    private Map<String, LoggedTimeData> getSubmittedTime(List<Worklog> userWorklogs, Stream<LocalDate> dates) {
        var reportedTime = userWorklogs.stream()
                .collect(Collectors.groupingBy(log -> log.getSubmissionDate().toLocalDate()));

        return dates
                .filter(
                        date -> date.isBefore(LocalDate.now().plusDays(1)) ||
                                getSubmittedSeconds(reportedTime.get(date)) > 0
                )
                .collect(Collectors.toMap(
                        TimeUtils::getDisplayDay,
                        date -> getTotalTime(reportedTime.get(date))));
    }

    private LoggedTimeData getTotalTime(List<Worklog> userWorklogs) {
        var totalSeconds = getSubmittedSeconds(userWorklogs);

        var totalMinutes = totalSeconds / 60;
        var hoursPart = totalMinutes / 60;
        var minutesPart = totalMinutes % 60;

        var result = new LoggedTimeData();
        result.setHours(String.valueOf(hoursPart));
        result.setMinutes(minutesPart != 0 ? String.valueOf(minutesPart) : "");
        return result;
    }

    private long getSubmittedSeconds(List<Worklog> userWorklogs) {
        if (userWorklogs == null) {
            return 0;
        }

        return userWorklogs.stream()
                .mapToInt(Worklog::getReportedSeconds)
                .sum();
    }
}
