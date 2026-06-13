package com.example.InventoryService.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.sharedevents.SeatLockedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatLockedProducer {

    private final KafkaTemplate<
                String,
            SeatLockedEvent> kafkaTemplate;

    public void publish(
            SeatLockedEvent event) {

        kafkaTemplate.send(
                "seat-locked",
                event
        );
    }
}