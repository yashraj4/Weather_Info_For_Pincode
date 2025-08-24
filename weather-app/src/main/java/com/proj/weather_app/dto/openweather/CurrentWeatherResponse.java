package com.proj.weather_app.dto.openweather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeatherResponse(List<WeatherItem> weather, Main main, Wind wind) {}