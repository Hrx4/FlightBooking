package com.example.InventoryService.controller;

import com.example.InventoryService.dto.CreateFlightRequest;
import com.example.InventoryService.entity.Flight;
import com.example.InventoryService.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<Flight> createFlight(
            @RequestBody CreateFlightRequest request) {

        return ResponseEntity.ok(
                flightService.createFlight(request)
        );
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<Flight> getFlight(
            @PathVariable String flightId) {

        return ResponseEntity.ok(
                flightService.getFlight(flightId)
        );
    }
}