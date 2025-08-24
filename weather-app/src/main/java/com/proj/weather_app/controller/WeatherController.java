package com.proj.weather_app.controller;

import com.proj.weather_app.dto.WeatherApiResponse;
import com.proj.weather_app.service.WeatherService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/weather")
@Validated
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public Mono<ResponseEntity<WeatherApiResponse>> getWeather(
            @RequestParam @Pattern(regexp = "^\\d{6}$", message = "Pincode must be a 6-digit number.") String pincode,
            @RequestParam @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in YYYY-MM-DD format.") String for_date) {
        
        return weatherService.getWeather(pincode, for_date)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}