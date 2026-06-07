package com.example.PaymentService.service;

import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.dto.PaymentResponse;
import com.example.PaymentService.dto.RefundRequest;

public interface PaymentService {

    PaymentResponse processPayment(
            PaymentRequest request
    );

    PaymentResponse refundPayment(
            RefundRequest request
    );

    PaymentResponse getPayment(
            String bookingId
    );
}