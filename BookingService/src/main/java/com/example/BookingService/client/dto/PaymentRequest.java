package com.example.BookingService.client.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    private String bookingId;

    private String userId;

    private BigDecimal amount;
}
