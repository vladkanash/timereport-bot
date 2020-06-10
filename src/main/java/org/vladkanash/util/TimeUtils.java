package org.vladkanash.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;

public class TimeUtils {

    public static boolean isInCurrentWeek(Date date) {
        var isAfterWeekStart = date.after(TimeUtils.toDate(TimeUtils.getCurrentDayOfWeekDate(1)));
        var isBeforeWeekEnd = date.before(TimeUtils.toDate(TimeUtils.getCurrentDayOfWeekDate(7)));

        return isAfterWeekStart && isBeforeWeekEnd;
    }

    public static String getFirstDayOfCurrentWeek() {
        return getCurrentDayOfWeekDate(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getLastDayOfCurrentWeek() {
        return getCurrentDayOfWeekDate(7).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String getDisplayDay(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEE, dd"));
    }

    public static String getDisplayMonth(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    public static LocalDate getCurrentDayOfWeekDate(int dayOfWeek) {
        LocalDate now = LocalDate.now();
        TemporalField tempField = WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek();
        return now.with(tempField, dayOfWeek);
    }

    public static LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
