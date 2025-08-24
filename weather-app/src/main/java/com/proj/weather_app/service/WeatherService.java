package com.proj.weather_app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.proj.weather_app.dto.Forecast;
import com.proj.weather_app.dto.Location;
import com.proj.weather_app.dto.Temperature;
import com.proj.weather_app.dto.WeatherApiResponse;
import com.proj.weather_app.dto.openweather.CurrentWeatherResponse;
import com.proj.weather_app.dto.openweather.GeocodingResponse;
import com.proj.weather_app.model.PincodeCache;
import com.proj.weather_app.respository.PincodeRepository;

import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private final PincodeRepository pincodeRepository;
    private final WebClient webClient;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    public WeatherService(PincodeRepository pincodeRepository, WebClient.Builder webClientBuilder, @Value("${openweathermap.api.base-url}") String apiUrl) {
        this.pincodeRepository = pincodeRepository;
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public Mono<WeatherApiResponse> getWeather(String pincode, String forDate) {
        // Step 1: Check cache for pincode coordinates
        Optional<PincodeCache> cachedPincode = pincodeRepository.findByPincode(pincode);

        if (cachedPincode.isPresent()) {
            System.out.println("CACHE HIT for pincode: " + pincode);
            PincodeCache location = cachedPincode.get();
            return fetchWeatherByCoords(location.getLatitude(), location.getLongitude())
                    .map(weather -> buildResponse("CACHE", location, weather, forDate));
        } else {
            System.out.println("CACHE MISS for pincode: " + pincode);
            return fetchCoordsByPincode(pincode)
                    .flatMap(location -> {
                        // Save the newly fetched geocoding data
                        pincodeRepository.save(location);
                        // Now fetch weather using the new coordinates
                        return fetchWeatherByCoords(location.getLatitude(), location.getLongitude())
                                .map(weather -> buildResponse("API", location, weather, forDate));
                    });
        }
    }

    private Mono<PincodeCache> fetchCoordsByPincode(String pincode) {
        return webClient.get()
                .uri("/geo/1.0/zip", uriBuilder -> uriBuilder
                        .queryParam("zip", pincode + ",in")
                        .queryParam("appid", apiKey).build())
                .retrieve()
                .bodyToMono(GeocodingResponse.class)
                .map(geo -> {
                    PincodeCache cache = new PincodeCache();
                    cache.setPincode(pincode);
                    cache.setCity(geo.city());
                    cache.setLatitude(geo.latitude());
                    cache.setLongitude(geo.longitude());
                    cache.setCountry(geo.country());
                    return cache;
                });
    }

    private Mono<CurrentWeatherResponse> fetchWeatherByCoords(double lat, double lon)
{
        return webClient.get()
                .uri("/data/2.5/weather", uriBuilder -> uriBuilder
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric").build())
                .retrieve()
                .bodyToMono(CurrentWeatherResponse.class);
    }

    private WeatherApiResponse buildResponse(String source, PincodeCache location, CurrentWeatherResponse weather, String forDate) {
        Location loc = new Location(location.getPincode(), location.getCity(), location.getCountry(), location.getLatitude(), location.getLongitude());
        Temperature temp = new Temperature(weather.main().temp(), weather.main().feels_like(), "celsius");
        Forecast forecast = new Forecast(forDate, weather.weather().get(0).description(), temp, weather.main().humidity(), weather.wind().speed());
        return new WeatherApiResponse(source, loc, forecast);
    }
}