package com.example.weathercollaborativeapp.adapter;

import android.os.Build;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime> {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date = dateFormat.parse(dateString);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return date != null ? LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()) : null;
            }
        } catch (ParseException e) {
            throw new JsonParseException("Unable to parse LocalDateTime", e);
        }
        return null;
    }
}
