package com.example.InventoryService.kafka.consumer;

import com.example.InventoryService.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingConfirmedConsumer {

    private final SeatService seatService;

    @KafkaListener(
            topics = "booking-confirmed",
            groupId = "inventory-group"
    )
    public void consume(
            BookingConfirmedEvent event) {

        seatService.confirmSeat(
                event.getFlightId(),
                event.getSeatNumber()
        );
    }
}