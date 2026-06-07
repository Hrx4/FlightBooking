package com.example.PaymentService.controller;

import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.dto.PaymentResponse;
import com.example.PaymentService.dto.RefundRequest;
import com.example.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponse>
    processPayment(
            @RequestBody PaymentRequest request){

        return ResponseEntity.ok(
                paymentService.processPayment(
                        request
                )
        );
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse>
    refundPayment(
            @RequestBody RefundRequest request){

        return ResponseEntity.ok(
                paymentService.refundPayment(
                        request
                )
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<PaymentResponse>
    getPayment(
            @PathVariable String bookingId){

        return ResponseEntity.ok(
                paymentService.getPayment(
                        bookingId
                )
        );
    }
}