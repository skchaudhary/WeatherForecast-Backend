package com.weather.forecast.services.impl;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.prominence.openweathermap.api.model.forecast.Forecast;
import com.github.prominence.openweathermap.api.model.forecast.WeatherForecast;
import com.github.prominence.openweathermap.api.model.onecall.historical.HistoricalWeatherData;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import com.weather.forecast.models.WeatherModel;
import com.weather.forecast.models.WeatherType;
import com.weather.forecast.repository.WeatherRepository;
import com.weather.forecast.services.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService{

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = {"#{'${app.kafka.consumer.topic}'.split(',')}"})
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        log.info("message received: {}", consumerRecord);
        String topic = consumerRecord.topic();
        String value = consumerRecord.value();
        String key = null;
        WeatherType type = null;
        if (topic.contains("current")) {
            Weather weather= objectMapper.convertValue(value, Weather.class);
            key = weather.getLocation() + LocalDateTime.now().toString();
            type = WeatherType.CURRENT;
        } else if (topic.contains("future")) {
            Forecast weather= objectMapper.convertValue(value, new Forecast());
            key = weather.getLocation() + LocalDateTime.now().toString();
            type = WeatherType.FUTURE;
        } else if (topic.contains("past")) {
            HistoricalWeatherData weather= objectMapper.convertValue(value, HistoricalWeatherData.class);
            key = weather.getCoordinate().toString() + LocalDateTime.now().toString();
            type = WeatherType.PAST;
        }
        weatherRepository.save(new WeatherModel(type, key, consumerRecord.value()));
    }
}
