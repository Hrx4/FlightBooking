package com.example.InventoryService.exception;

public class SeatReservationConflictException extends RuntimeException {
    public SeatReservationConflictException(String message) {
        super(message);
    }
}
