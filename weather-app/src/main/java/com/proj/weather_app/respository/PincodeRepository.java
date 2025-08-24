package com.proj.weather_app.respository;

import com.proj.weather_app.model.PincodeCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PincodeRepository extends JpaRepository<PincodeCache, String> {
    Optional<PincodeCache> findByPincode(String pincode);
}