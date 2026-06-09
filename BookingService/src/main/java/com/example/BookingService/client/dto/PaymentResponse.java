package com.example.BookingService.client.dto;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentId;

    private String bookingId;

    private String status;

    private String message;
}
