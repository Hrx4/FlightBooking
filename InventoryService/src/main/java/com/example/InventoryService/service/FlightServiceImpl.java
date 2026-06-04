package com.example.InventoryService.service;

import com.example.InventoryService.dto.CreateFlightRequest;
import com.example.InventoryService.entity.Flight;
import com.example.InventoryService.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Flight createFlight(CreateFlightRequest request) {

        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .source(request.getSource())
                .destination(request.getDestination())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .build();

        return flightRepository.save(flight);
    }

    @Override
    public Flight getFlight(String flightId) {

        return flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found"
                        ));
    }
}
