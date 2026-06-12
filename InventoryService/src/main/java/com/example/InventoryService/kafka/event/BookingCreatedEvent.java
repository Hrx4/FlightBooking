package com.example.InventoryService.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreatedEvent {

    private String bookingId;

    private String flightId;

    private String seatNumber;

    private String userId;

    private BigDecimal amount;
}

