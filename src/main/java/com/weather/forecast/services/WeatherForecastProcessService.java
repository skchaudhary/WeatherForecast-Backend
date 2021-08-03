package com.weather.forecast.services;

import com.weather.forecast.models.WeatherModel;

import java.util.Optional;

public interface WeatherForecastProcessService {
    Optional<WeatherModel> getCurrentWeatherForecast(String city);
    Optional<WeatherModel> getPastWeatherForecast(Double latitude, Double longitude);
    Optional<WeatherModel> getFutureWeatherForecast(String city);
}
