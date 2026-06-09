package com.example.InventoryService.repository;

import com.example.InventoryService.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository
        extends JpaRepository<Seat,String> {

    List<Seat> findByFlightId(String flightId);

    Optional<Seat> findByFlight_IdAndSeatNumber(
            String flightId,
            String seatNumber
    );
}