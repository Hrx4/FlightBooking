package com.example.InventoryService.repository;

import com.example.InventoryService.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository
        extends JpaRepository<Flight,String> {

    Optional<Flight> findByFlightNumber(String flightNumber);
}