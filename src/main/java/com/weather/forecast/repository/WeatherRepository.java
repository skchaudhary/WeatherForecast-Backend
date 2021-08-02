package com.weather.forecast.repository;

import com.weather.forecast.models.Customer;
import com.weather.forecast.models.WeatherModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WeatherRepository extends MongoRepository<WeatherModel, String> {
    Optional<WeatherModel> findByKey(String key);
}
