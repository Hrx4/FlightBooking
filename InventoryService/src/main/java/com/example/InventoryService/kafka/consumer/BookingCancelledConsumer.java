package com.example.InventoryService.kafka.consumer;

import com.example.InventoryService.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingCancelledEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BookingCancelledConsumer {

    private final SeatService seatService;

    @KafkaListener(
            topics = "booking-cancelled",
            groupId = "inventory-group")
    public void handleBookingCancelled(
            BookingCancelledEvent event) {

        seatService.releaseSeat(
                event.getFlightId(),
                event.getSeatNumber());
    }

}
