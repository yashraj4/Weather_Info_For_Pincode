package com.proj.weather_app.dto.openweather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResponse(
    @JsonProperty("zip") String pincode,
    @JsonProperty("name") String city,
    @JsonProperty("lat") double latitude,
    @JsonProperty("lon") double longitude,
    @JsonProperty("country") String country
) {}