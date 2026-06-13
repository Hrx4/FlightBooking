package com.example.InventoryService.kafka.consumer;

import com.example.InventoryService.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.PaymentFailedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final SeatService seatService;

    @KafkaListener(
            topics = "payment-failed",
            groupId = "inventory-group"
    )
    public void consume(
            PaymentFailedEvent event) {

        seatService.releaseSeat(
                event.getFlightId(),
                event.getSeatNumber()
        );

        System.out.println(
                "Seat released for booking: "
                        + event.getBookingId()
        );
    }
}
