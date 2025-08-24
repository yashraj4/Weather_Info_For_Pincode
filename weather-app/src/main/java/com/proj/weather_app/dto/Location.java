package com.proj.weather_app.dto;

// This is the correct, simple syntax for a record. No semicolon or body.
public record Location(String pincode, String city, String country, double latitude, double longitude) {}