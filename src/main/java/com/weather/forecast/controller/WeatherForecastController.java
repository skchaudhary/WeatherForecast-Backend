package com.weather.forecast.controller;

import com.weather.forecast.services.KafkaConsumerService;
import com.weather.forecast.services.KafkaProducerService;
import com.weather.forecast.utils.WeatherProcessUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaConsumerService kafkaConsumerService;

    /**
     * forecast current day weather data
     *
     * @return
     */
    @GetMapping("/current/{cityName}")
    public String currentWeather(@PathVariable String cityName) throws IOException {
        currentWeatherUrl = String.format(currentWeatherUrl, cityName);
        String data = WeatherProcessUtil.getWeatherResponse(currentWeatherUrl);
        kafkaProducerService.produce(data);

        return data;
    }

    /**
     * forecast future weeks weather data
     *
     * @return
     */
    @GetMapping("/future")
    public String forecastFutureWeather() throws IOException {
        return WeatherProcessUtil.getWeatherResponse(futureWeatherUrl);
    }

    /**
     * forecast past weeks weather data
     *
     * @return
     */
    @GetMapping("/past")
    public String forecastPastWeather() throws IOException {
        return WeatherProcessUtil.getWeatherResponse(pastWeatherUrl);
    }
}
