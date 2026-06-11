package com.example.BookingService.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateBookingRequest {

    private String idempotencyKey;

    private String flightId;

    private String seatNumber;

    private BigDecimal amount;
}