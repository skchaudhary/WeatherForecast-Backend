package com.weather.forecast.services.impl;

import com.weather.forecast.services.KafkaConsumerService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService{
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

    @Override
    @KafkaListener(topics = {"#{'${app.kafka.consumer.topic}'.split(',')}"})
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        logger.info("message received: {}", consumerRecord);
    }
}
