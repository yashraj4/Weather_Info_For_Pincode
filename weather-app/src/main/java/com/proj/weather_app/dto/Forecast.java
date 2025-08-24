package com.proj.weather_app.dto;

public record Forecast(String date, String condition, Temperature temperature, int humidity_percent, double wind_kph) {}