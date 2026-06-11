package com.example.BookingService.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestTemplate restTemplate;

    private static final String BASE_URL =
            "http://localhost:8082/api/seats";

    @Retry(name = "inventoryRetry")
    @CircuitBreaker(
            name = "inventoryCircuit",
            fallbackMethod = "inventoryFallback"
    )
    public void lockSeat(
            String flightId,
            String seatNumber) {

        restTemplate.postForObject(
                BASE_URL + "/lock",
                Map.of(
                        "flightId", flightId,
                        "seatNumber", seatNumber
                ),
                String.class
        );
    }

    public void confirmSeat(
            String flightId,
            String seatNumber) {

        restTemplate.postForObject(
                BASE_URL + "/confirm",
                Map.of(
                        "flightId", flightId,
                        "seatNumber", seatNumber
                ),
                String.class
        );
    }

    public void inventoryFallback(
            String flightId,
            String seatNumber,
            Exception ex) {

        throw new RuntimeException(
                "Inventory Service Unavailable"
        );
    }

    public void releaseSeat(
            String flightId,
            String seatNumber) {

        restTemplate.postForObject(
                BASE_URL + "/release",
                Map.of(
                        "flightId", flightId,
                        "seatNumber", seatNumber
                ),
                String.class
        );
    }
}