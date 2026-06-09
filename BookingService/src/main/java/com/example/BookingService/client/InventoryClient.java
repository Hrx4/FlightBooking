package com.example.BookingService.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class InventoryClient {

    private final RestTemplate restTemplate;

    private static final String BASE_URL =
            "http://localhost:8083/api/seats";

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