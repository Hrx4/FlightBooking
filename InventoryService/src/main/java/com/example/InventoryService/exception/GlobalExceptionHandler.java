package com.example.InventoryService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            FlightNotFoundException.class
    )
    public ResponseEntity<String> handleFlightNotFound(
            FlightNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(
            SeatNotFoundException.class
    )
    public ResponseEntity<String> handleSeatNotFound(
            SeatNotFoundException ex) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(
            SeatAlreadyBookedException.class
    )
    public ResponseEntity<String> handleSeatBooked(
            SeatAlreadyBookedException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}