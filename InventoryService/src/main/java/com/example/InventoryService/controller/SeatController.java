package com.example.InventoryService.controller;

import com.example.InventoryService.dto.CreateSeatsRequest;
import com.example.InventoryService.dto.SeatReservationRequest;
import com.example.InventoryService.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping("/{flightId}")
    public ResponseEntity<String> createSeats(
            @PathVariable String flightId,
            @RequestBody CreateSeatsRequest request) {

        seatService.createSeats(flightId, request);

        return ResponseEntity.ok(
                "Seats created successfully"
        );
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveSeat(
            @RequestBody SeatReservationRequest request) {

        seatService.reserveSeat(
                request.getFlightId(),
                request.getSeatNumber()
        );

        return ResponseEntity.ok(
                "Seat reserved successfully"
        );
    }

    @PostMapping("/release")
    public ResponseEntity<String> releaseSeat(
            @RequestBody SeatReservationRequest request) {

        seatService.releaseSeat(
                request.getFlightId(),
                request.getSeatNumber()
        );

        return ResponseEntity.ok(
                "Seat released successfully"
        );
    }

    @PostMapping("/lock")
    public ResponseEntity<String> lockSeat(
            @RequestBody SeatReservationRequest request) {

        seatService.lockSeat(
                request.getFlightId(),
                request.getSeatNumber()
        );

        return ResponseEntity.ok(
                "Seat locked successfully"
        );
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmSeat(
            @RequestBody SeatReservationRequest request) {

        seatService.confirmSeat(
                request.getFlightId(),
                request.getSeatNumber()
        );

        return ResponseEntity.ok(
                "Seat confirmed successfully"
        );
    }
}