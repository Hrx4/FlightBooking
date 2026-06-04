package com.example.InventoryService.dto;

import lombok.Data;

@Data
public class SeatReservationRequest {

    private String flightId;

    private String seatNumber;
}