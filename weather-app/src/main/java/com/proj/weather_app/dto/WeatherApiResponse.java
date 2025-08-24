package com.proj.weather_app.dto;

public record WeatherApiResponse(
    String dataSource,
    Location location,
    Forecast forecast
) {}