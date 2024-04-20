package com.example.weathercollaborativeapp.utils;

import android.os.Build;
import android.text.format.DateUtils;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getRelativeTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);

        if (seconds < 60) {
            return "Posté il y a " + seconds + " secondes";
        } else if (minutes < 60) {
            return "Posté il y a " + minutes + " minutes";
        } else if (hours < 24) {
            return "Posté il y a " + hours + " heures " + (minutes % 60) + " minutes";
        } else {
            return "Posté il y a " + days + " jours";
        }
    }
}
