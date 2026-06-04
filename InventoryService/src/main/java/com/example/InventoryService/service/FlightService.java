package com.example.InventoryService.service;

import com.example.InventoryService.dto.CreateFlightRequest;
import com.example.InventoryService.entity.Flight;

public interface FlightService {

    Flight createFlight(
            CreateFlightRequest request
    );

    Flight getFlight(
            String flightId
    );
}