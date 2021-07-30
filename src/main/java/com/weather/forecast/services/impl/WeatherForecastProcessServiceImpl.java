package com.weather.forecast.services.impl;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeatherData;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import com.weather.forecast.services.WeatherForecastProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class WeatherForecastProcessServiceImpl implements WeatherForecastProcessService {

    @Value("${weather.api.token}")
    private String apiKey;

    OpenWeatherMapClient openWeatherClient = new OpenWeatherMapClient(apiKey);

    @Override
    public Optional<Weather> getCurrentWeatherForecast(String city) {
        CompletableFuture<Weather> completableFuture = openWeatherClient.currentWeather()
                .single()
                .byCityName(city)
                .language(Language.ENGLISH)
                .retrieveAsync()
                .asJava();
        Optional<Weather> currentWeather = Optional.empty();
        try {
            return Optional.of(completableFuture.get());
        } catch (InterruptedException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        }
        return currentWeather;
    }

    @Override
    public Optional<HistoricalWeatherData> getPastWeatherForecast(Double lat, Double lon) {
        CompletableFuture<HistoricalWeatherData> completableFuture = openWeatherClient.oneCall()
                .historical()
                .byCoordinateAndTimestamp(Coordinate.of(lat, lon), LocalDateTime.now().minusDays(5).toEpochSecond(ZoneOffset.UTC))
                .language(Language.ENGLISH)
                .retrieveAsync()
                .asJava();
        Optional<HistoricalWeatherData> historicalWeatherData = Optional.empty();
        try {
            return Optional.of(completableFuture.get());
        } catch (InterruptedException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        }
        return historicalWeatherData;
    }

    @Override
    public Optional<Forecast> getFutureWeatherForecast(String city) {
        Forecast forecast = openWeatherClient.forecast5Day3HourStep()
                .byCityName(city)
                .count(15)
                .language(Language.ENGLISH)
                .retrieve()
                .asJava();
        Optional<Forecast> futureForecastedData = Optional.empty();
        if (forecast != null) {
            return Optional.of(forecast);
        }
        return futureForecastedData;
    }
}
