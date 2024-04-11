package com.example.weathercollaborativeapp.network;

import com.example.weathercollaborativeapp.model.WeatherType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WeatherService {
    @GET("weatherTypes")
    Call<List<WeatherType>> listWeatherTypes();
}
