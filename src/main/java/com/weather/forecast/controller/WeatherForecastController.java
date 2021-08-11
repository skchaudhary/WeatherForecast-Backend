package com.weather.forecast.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.forecast.models.WeatherModel;
import com.weather.forecast.services.WeatherForecastProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin
public class WeatherForecastController {

    @Value("${weather.current}")
    private String currentWeatherUrl;

    @Value("${weather.past}")
    private String pastWeatherUrl;

    @Value("${weather.future}")
    private String futureWeatherUrl;

    @Autowired
    private WeatherForecastProcessService weatherForecastProcessService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * forecast current day weather data
     *
     * @return
     */
    @GetMapping("/weather/forecast/current/{cityName}")
    public ResponseEntity<String> currentWeather(@PathVariable String cityName) throws IOException {
        Optional<WeatherModel> currentForecastedData = weatherForecastProcessService.getCurrentWeatherForecast(cityName);
        if (currentForecastedData.isPresent()) {
            return ResponseEntity.ok().body(objectMapper.writeValueAsString(currentForecastedData.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }
    }

    /**
     * forecast future weeks weather data
     *
     * @return
     */
    @GetMapping("/weather/forecast/future/{cityName}")
    public String forecastFutureWeather(@PathVariable String cityName) throws IOException {
        Optional<WeatherModel> currentForecastedData = weatherForecastProcessService.getFutureWeatherForecast(cityName);
        if (currentForecastedData.isPresent()) {
            return objectMapper.writeValueAsString(currentForecastedData.get());
        } else {
            return new String();
        }
    }

    /**
     * forecast past weeks weather data
     *
     * @return
     */
    @GetMapping("/weather/forecast/past/{latitude}/{longitude}")
    public String forecastPastWeather(@PathVariable Double latitude, @PathVariable Double longitude) throws IOException {
        Optional<WeatherModel> currentForecastedData = weatherForecastProcessService.getPastWeatherForecast(latitude, longitude);
        if (currentForecastedData.isPresent()) {
            return objectMapper.writeValueAsString(currentForecastedData.get());
        } else {
            return new String();
        }
    }
}
