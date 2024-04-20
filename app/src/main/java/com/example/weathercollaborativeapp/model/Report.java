package com.example.weathercollaborativeapp.model;

import java.time.LocalDateTime;

public class Report {


    private double latitude;
    private double longitude;

    public Report(LocalDateTime createdAt, double latitude, double longitude, double temperature,  WeatherType weatherType) {
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.weatherType = weatherType;
    }

    private double temperature;
    private LocalDateTime createdAt;
    private WeatherType weatherType;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public WeatherType getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(WeatherType weatherType) {
        this.weatherType = weatherType;
    }
}
