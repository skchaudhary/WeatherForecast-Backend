package com.weather.forecast.repository;

import com.weather.forecast.models.WeatherModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WeatherRepository extends MongoRepository<WeatherModel, String> {
    List<WeatherModel> findByKey(String key);
}
