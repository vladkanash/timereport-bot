package org.vladkanash.jira.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.vladkanash.util.Config.CONFIG;

public class RequestUtils {

    public static String getWorklogSearchQuery(List<String> userIds, LocalDate startDate, LocalDate endDate) {
        var rawQuery = CONFIG.get("jira.rest.worklog.query");

        var isoStartDate = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        var isoEndDate = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        var escapedUsers = userIds
                .stream()
                .map(user -> "'" + user + "'")
                .collect(Collectors.joining(", "));

        return String.format(rawQuery, escapedUsers, isoStartDate, isoEndDate);
    }
}
