package com.proj.weather_app.respository;

import com.proj.weather_app.entity.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {
    Optional<WeatherEntity> findByPincodeAndDate(String pincode, LocalDate date);
}
