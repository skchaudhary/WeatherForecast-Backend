package com.weather.forecast.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.forecast.models.WeatherModel;
import com.weather.forecast.repository.WeatherRepository;
import com.weather.forecast.services.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = {"#{'${app.kafka.consumer.topic}'.split(',')}"})
    public void consume(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        log.info("message received: {}", consumerRecord);
        String value = consumerRecord.value();
        WeatherModel weatherModel = null;
        if (value != null) {
            weatherModel = objectMapper.readValue(value, WeatherModel.class);
        }
        if (weatherModel != null) {
            weatherRepository.save(weatherModel);
        }
    }
}
