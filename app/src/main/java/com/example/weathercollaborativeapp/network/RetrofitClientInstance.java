package com.example.weathercollaborativeapp.network;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.weathercollaborativeapp.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Retrofit getRetrofitInstance(){

        Gson gson = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
        }

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }

}
