package com.weather.forecast.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumerService {
    void consume(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException;
}
