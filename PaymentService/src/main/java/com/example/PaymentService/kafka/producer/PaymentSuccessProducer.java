package com.example.PaymentService.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.sharedevents.PaymentSuccessEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentSuccessProducer {

    private final KafkaTemplate<
                String,
            PaymentSuccessEvent> kafkaTemplate;

    public void publish(
            PaymentSuccessEvent event) {

        kafkaTemplate.send(
                "payment-success",
                event
        );
    }
}