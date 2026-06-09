package com.example.BookingService.client;

import com.example.BookingService.client.dto.PaymentRequest;
import com.example.BookingService.client.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    private static final String BASE_URL =
            "http://localhost:8084/api/payments";

    public PaymentResponse processPayment(
            PaymentRequest request) {

        return restTemplate.postForObject(
                BASE_URL + "/process",
                request,
                PaymentResponse.class
        );
    }
}
