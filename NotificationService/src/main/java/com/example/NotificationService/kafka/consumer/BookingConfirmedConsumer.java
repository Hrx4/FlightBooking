package com.example.NotificationService.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.example.sharedevents.BookingConfirmedEvent;

@Component
@Slf4j
public class BookingConfirmedConsumer {

    @KafkaListener(
            topics = "booking-confirmed",
            groupId = "notification-group"
    )
    public void consume(
            BookingConfirmedEvent event) {

        log.info(
                "Sending confirmation email for booking {}",
                event.getBookingId()
        );
    }
}