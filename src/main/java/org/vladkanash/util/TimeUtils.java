package org.vladkanash.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;

public class TimeUtils {

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

    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
