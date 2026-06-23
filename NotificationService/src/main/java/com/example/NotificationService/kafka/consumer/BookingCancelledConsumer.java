package com.example.NotificationService.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.sharedevents.BookingCancelledEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingCancelledConsumer {

    @KafkaListener(
            topics = "booking-cancelled",
            groupId = "notification-group"
    )
    public void consume(
            BookingCancelledEvent event) {

        log.info(
                "Sending cancellation email for booking {}",
                event.getBookingId()
        );
    }
}