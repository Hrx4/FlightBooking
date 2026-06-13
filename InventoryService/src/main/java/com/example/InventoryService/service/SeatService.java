package com.example.InventoryService.service;

import com.example.InventoryService.dto.CreateSeatsRequest;

public interface SeatService {

    void createSeats(
            String flightId,
            CreateSeatsRequest request
    );

    void reserveSeat(
            String flightId,
            String seatNumber
    );

    void releaseSeat(
            String flightId,
            String seatNumber
    );

    void lockSeat(
            String flightId,
            String seatNumber);

    void confirmSeat(
            String flightId,
            String seatNumber
    );

}