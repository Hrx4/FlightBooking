package com.example.BookingService.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingConfirmedProducer {

    private final KafkaTemplate<
                String,
            BookingConfirmedEvent> kafkaTemplate;

    public void publish(
            BookingConfirmedEvent event) {

        kafkaTemplate.send(
                "booking-confirmed",
                event
        );
    }
}