package com.example.PaymentService.service;

import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.dto.PaymentResponse;
import com.example.PaymentService.dto.RefundRequest;
import com.example.PaymentService.entity.Payment;
import com.example.PaymentService.entity.PaymentStatus;
import com.example.PaymentService.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl
        implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse processPayment(
            PaymentRequest request) {

        boolean success =
//                true;
                new Random().nextBoolean();

        Payment payment =
                Payment.builder()
                        .bookingId(
                                request.getBookingId())
                        .userId(
                                request.getUserId())
                        .amount(
                                request.getAmount())
                        .status(
                                success
                                        ? PaymentStatus.SUCCESS
                                        : PaymentStatus.FAILED)
                        .createdAt(
                                LocalDateTime.now())
                        .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .status(payment.getStatus())
                .message(
                        success
                                ? "Payment Successful"
                                : "Payment Failed")
                .build();
    }

    @Override
    public PaymentResponse refundPayment(
            RefundRequest request) {

        Payment payment =
                paymentRepository
                        .findByBookingId(
                                request.getBookingId())
                        .orElseThrow();

        payment.setStatus(
                PaymentStatus.REFUNDED);

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .status(payment.getStatus())
                .message("Refund Successful")
                .build();
    }

    @Override
    public PaymentResponse getPayment(
            String bookingId) {

        Payment payment =
                paymentRepository
                        .findByBookingId(
                                bookingId)
                        .orElseThrow();

        return PaymentResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .status(payment.getStatus())
                .message("Payment Found")
                .build();
    }
}