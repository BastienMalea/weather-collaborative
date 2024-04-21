package com.example.weathercollaborativeapp.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getRelativeTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        Log.d("tutu", "Created At : " + createdAt);
        Log.d("tutu", "Seconde " + seconds + " minute : " + minutes + " Heures " + hours + " days : " + days);
        if (seconds < 60) {
            return "Il y a " + seconds + " s";
        } else if (minutes < 60) {
            return "Il y a " + minutes + " min";
        } else if (hours < 24) {
            return "Il y a " + hours + " H " + (minutes % 60) + " min";
        } else {
            return "Il y a" + days + " j";
        }
    }
}
