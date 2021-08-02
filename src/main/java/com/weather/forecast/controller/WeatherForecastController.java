package com.weather.forecast.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeatherData;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import com.weather.forecast.services.KafkaConsumerService;
import com.weather.forecast.services.KafkaProducerService;
import com.weather.forecast.services.WeatherForecastProcessService;
import com.weather.forecast.utils.WeatherProcessUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/weather/forecast")
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
    @GetMapping("/current/{cityName}")
    public String currentWeather(@PathVariable String cityName) throws IOException {
//        currentWeatherUrl = String.format(currentWeatherUrl, cityName);
//        String data = WeatherProcessUtil.getWeatherResponse(currentWeatherUrl);
//        kafkaProducerService.produce(data);

        Optional<Weather> currentForecastedData = weatherForecastProcessService.getCurrentWeatherForecast(cityName);
        if (currentForecastedData.isPresent()) {
            return objectMapper.writeValueAsString(currentForecastedData.get());
        } else {
            return new String();
        }
    }

    /**
     * forecast future weeks weather data
     *
     * @return
     */
    @GetMapping("/future/{cityName}")
    public String forecastFutureWeather(@PathVariable String cityName) throws IOException {
        Optional<Forecast> currentForecastedData = weatherForecastProcessService.getFutureWeatherForecast(cityName);
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
    @GetMapping("/past/{latitude}/{longitude}")
    public String forecastPastWeather(@PathVariable Double latitude, @PathVariable Double longitude) throws IOException {
        Optional<HistoricalWeatherData> currentForecastedData = weatherForecastProcessService.getPastWeatherForecast(latitude, longitude);
        if (currentForecastedData.isPresent()) {
            return objectMapper.writeValueAsString(currentForecastedData.get());
        } else {
            return new String();
        }
    }
}
