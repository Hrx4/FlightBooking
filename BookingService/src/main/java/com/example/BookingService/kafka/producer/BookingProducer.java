package com.example.BookingService.kafka.producer;

import com.example.BookingService.kafka.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingProducer {

    private final KafkaTemplate<
            String,
            BookingCreatedEvent> kafkaTemplate;

    public void publish(
            BookingCreatedEvent event) {

        kafkaTemplate.send(
                "booking-created",
                event
        );
    }
}