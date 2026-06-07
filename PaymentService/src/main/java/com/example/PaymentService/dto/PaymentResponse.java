package com.example.PaymentService.dto;

import com.example.PaymentService.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {

    private String paymentId;

    private String bookingId;

    private PaymentStatus status;

    private String message;
}