package org.vladkanash.jira.util;

import org.vladkanash.util.TimeUtils;

import java.util.stream.Collectors;

import static org.vladkanash.util.Config.CONFIG;

public class RequestUtils {

    public static String getWorklogSearchQuery() {
        var users = CONFIG.getList("jira.users.list");
        var rawQuery = CONFIG.get("jira.rest.worklog.query");
        var firstDayOfWeek = TimeUtils.getFirstDayOfCurrentWeek();
        var lastDayOfWeek = TimeUtils.getLastDayOfCurrentWeek();

        var escapedUsers = users
                .stream()
                .map(user -> "'" + user + "'")
                .collect(Collectors.joining(", "));

        return String.format(rawQuery, escapedUsers, firstDayOfWeek, lastDayOfWeek);
    }
}
