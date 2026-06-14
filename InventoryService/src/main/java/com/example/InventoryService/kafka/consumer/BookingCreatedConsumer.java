package com.example.InventoryService.kafka.consumer;

import com.example.InventoryService.kafka.producer.SeatLockedProducer;
import com.example.InventoryService.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingCreatedEvent;
import org.example.sharedevents.SeatLockedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCreatedConsumer {

    private final SeatService seatService;
    private final SeatLockedProducer seatLockedProducer;

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
        System.out.println("After Seat lock : " + event.getSeatNumber() );

            seatLockedProducer.publish(
                    SeatLockedEvent.builder()
                            .bookingId(event.getBookingId())
                            .flightId(event.getFlightId())
                            .seatNumber(event.getSeatNumber())
                            .userId(event.getUserId())
                            .amount(event.getAmount())
                            .build()
            );
        } catch (Exception e) {
            System.out.println("Seat {} already booked, skipping event : " +  e.getMessage());
        }
    }
}