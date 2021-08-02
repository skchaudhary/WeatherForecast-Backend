package com.weather.forecast.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeatherData;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import com.weather.forecast.services.KafkaProducerService;
import com.weather.forecast.services.WeatherForecastProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${app.kafka.producer.topic-current}")
    private String currentDataTopic;

    @Value("${app.kafka.producer.topic-past}")
    private String pastDataTopic;

    @Value("${app.kafka.producer.topic-future}")
    private String futureDataTopic;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    OpenWeatherMapClient openWeatherClient;

    @Override
    public Optional<Weather> getCurrentWeatherForecast(String city) {
        openWeatherClient = new OpenWeatherMapClient(apiKey);
        CompletableFuture<Weather> completableFuture = openWeatherClient.currentWeather()
                .single()
                .byCityName(city)
                .language(Language.ENGLISH)
                .retrieveAsync()
                .asJava();
        Optional<Weather> weatherData = Optional.empty();
        try {
            Weather weather = completableFuture.get();
            kafkaProducerService.produce(objectMapper.writeValueAsString(weather), currentDataTopic);
            return Optional.of(weather);
        } catch (InterruptedException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error during parsing the weather data: {}", e.getMessage());
        }


        return weatherData;
    }

    @Override
    public Optional<HistoricalWeatherData> getPastWeatherForecast(Double latitude, Double longitude) {
        openWeatherClient = new OpenWeatherMapClient(apiKey);
        CompletableFuture<HistoricalWeatherData> completableFuture = openWeatherClient.oneCall()
                .historical()
                .byCoordinateAndTimestamp(Coordinate.of(latitude, longitude), LocalDateTime.now().minusDays(5).toEpochSecond(ZoneOffset.UTC))
                .language(Language.ENGLISH)
                .retrieveAsync()
                .asJava();
        Optional<HistoricalWeatherData> historicalWeatherData = Optional.empty();
        try {
            HistoricalWeatherData weatherData = completableFuture.get();
            kafkaProducerService.produce(objectMapper.writeValueAsString(weatherData), pastDataTopic);
            return Optional.of(weatherData);
        } catch (InterruptedException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Error during getting the current weather forecasted data with error: {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.error("Error during parsing the weather data: {}", e.getMessage());
        }
        return historicalWeatherData;
    }

    @Override
    public Optional<Forecast> getFutureWeatherForecast(String city) {
        openWeatherClient = new OpenWeatherMapClient(apiKey);
        Forecast forecast = openWeatherClient.forecast5Day3HourStep()
                .byCityName(city)
                .count(15)
                .language(Language.ENGLISH)
                .retrieve()
                .asJava();
        Optional<Forecast> futureForecastedData = Optional.empty();
        if (forecast != null) {
            try {
                kafkaProducerService.produce(objectMapper.writeValueAsString(forecast), futureDataTopic);
                return Optional.of(forecast);
            } catch (JsonProcessingException e) {
                log.error("Error during parsing the weather data: {}", e.getMessage());
            }
        }
        return futureForecastedData;
    }
}
