package com.weather.forecast.services;

import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeatherData;
import com.github.prominence.openweathermap.api.model.weather.Weather;

import java.util.Optional;

public interface WeatherForecastProcessService {
    Optional<Weather> getCurrentWeatherForecast(String city);
    Optional<HistoricalWeatherData> getPastWeatherForecast(Double lon, Double lat);
    Optional<Forecast> getFutureWeatherForecast(String city);
}
