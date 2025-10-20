package com.example.petapp.common.base.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtil {
    private static final DateTimeFormatter HM_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    public static String getTimeAgo(LocalDateTime localDateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(localDateTime, now);
        long hours = ChronoUnit.HOURS.between(localDateTime, now);
        long days = ChronoUnit.DAYS.between(localDateTime, now);
        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else {
            return days + "일 전";
        }
    }

    public static LocalDateTime convert(String hhmm) {
        LocalDate today = LocalDate.now(ZONE);
        LocalTime time = LocalTime.parse(hhmm, HM_FORMAT)
                .withSecond(0);
        return LocalDateTime.of(today, time);
    }
}
