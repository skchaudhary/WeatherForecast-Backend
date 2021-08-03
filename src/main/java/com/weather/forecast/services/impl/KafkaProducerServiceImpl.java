package com.weather.forecast.services.impl;

import com.weather.forecast.services.KafkaProducerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void produce(String message, String topic) {
        log.info("message before sent: {}", message);
        ProducerRecord<String, String> producerRecord = buildProducerRecord(topic, message);
        ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(producerRecord);
        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleFailure(topic, message);
            }

            @Override
            public void onSuccess(SendResult<String, String> message) {
                handleSuccess(topic, message);
            }
        });

    }

    private ProducerRecord<String, String> buildProducerRecord(String topic, String message) {
        return new ProducerRecord<>(topic, message);
    }

    private void handleSuccess(String topic, SendResult<String, String> message) {
        log.info("Message sent successfully with topic: {} and message: {}", topic, message);
    }

    private void handleFailure(String topic, String message) {
        log.error("Message Failed to send with topic: {} and message: {}", topic, message);
    }

}
