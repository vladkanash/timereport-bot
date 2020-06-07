package org.vladkanash.jira.util;

import org.vladkanash.util.TimeUtils;

import java.util.List;
import java.util.stream.Collectors;

public class RequestUtils {

    public static String createWorklogSearchQuery(String rawQuery, List<String> users) {
        var escapedUsers = users
                .stream()
                .map(user -> "'" + user + "'")
                .collect(Collectors.joining(", "));

        return String.format(rawQuery, escapedUsers, TimeUtils.getFirstDayOfCurrentWeek(), TimeUtils.getLastDayOfCurrentWeek());
    }
}
