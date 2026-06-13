package com.example.PaymentService.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.sharedevents.PaymentFailedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFailedProducer {

    private final KafkaTemplate<
                String,
            PaymentFailedEvent> kafkaTemplate;

    public void publish(
            PaymentFailedEvent event) {

        kafkaTemplate.send(
                "payment-failed",
                event
        );
    }
}