package com.example.InventoryService.kafka.consumer;

import com.example.InventoryService.kafka.event.BookingCreatedEvent;
import com.example.InventoryService.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCreatedConsumer {

    private final SeatService seatService;

    @KafkaListener(
            topics = "booking-created",
            groupId = "inventory-group"
    )
    public void consume(
            BookingCreatedEvent event) {

        System.out.println(
                "Received Booking Event"
        );

        System.out.println("Booking Created Event: " + event);

        try{
            seatService.lockSeat(
                    event.getFlightId(),
                    event.getSeatNumber()
            );
        } catch (Exception e) {
            System.out.println("Seat {} already booked, skipping event : " +  e.getMessage());
        }
    }
}