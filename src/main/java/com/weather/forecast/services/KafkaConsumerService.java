package com.weather.forecast.services;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumerService {
    void consume(ConsumerRecord<String, String> consumerRecord);
}
