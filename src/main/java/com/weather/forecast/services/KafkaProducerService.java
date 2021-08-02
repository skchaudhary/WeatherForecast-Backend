package com.weather.forecast.services;

public interface KafkaProducerService {
    void produce(String message, String topic);
}
