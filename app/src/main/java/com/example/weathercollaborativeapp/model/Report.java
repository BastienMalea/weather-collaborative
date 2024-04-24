package com.example.weathercollaborativeapp.model;

import com.example.weathercollaborativeapp.utils.LocationUtils;

import java.time.LocalDateTime;

public class Report {
    private long id;
    private double latitude;
    private double longitude;
    private String username;
    private double distanceFromUser;
    public double getDistanceFromUser() {
        return distanceFromUser;
    }
    public void setDistanceFromUser(double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }
    public Report(double latitude, double longitude, double temperature, String username, WeatherType weatherType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.username = username;
        this.weatherType = weatherType;
    }

    private double temperature;
    private LocalDateTime createdAt;
    private WeatherType weatherType;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
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
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public WeatherType getWeatherType() { return weatherType; }

    public void setWeatherType(WeatherType weatherType) { this.weatherType = weatherType; }

    public void updateDistanceFrom(double userLat, double userLon) {
        this.distanceFromUser = LocationUtils.calculateDistance(userLat, userLon, this.latitude, this.longitude);
    }
}
