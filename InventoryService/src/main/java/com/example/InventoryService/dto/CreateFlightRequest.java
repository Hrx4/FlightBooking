package com.example.InventoryService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateFlightRequest {

    private String flightNumber;

    private String source;

    private String destination;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;
}