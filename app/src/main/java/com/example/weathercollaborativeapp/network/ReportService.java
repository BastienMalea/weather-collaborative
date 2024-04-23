package com.example.weathercollaborativeapp.network;

import com.example.weathercollaborativeapp.model.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReportService {
    @GET("reports/nearby")
    Call<List<Report>> getNearbyReports(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") double radius);

    @POST("reports")
    Call<Report> postReport(@Body Report report);
}
