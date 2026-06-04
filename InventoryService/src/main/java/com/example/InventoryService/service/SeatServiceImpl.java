package com.example.InventoryService.service;

import com.example.InventoryService.dto.CreateSeatsRequest;
import com.example.InventoryService.entity.Flight;
import com.example.InventoryService.entity.Seat;
import com.example.InventoryService.entity.SeatStatus;
import com.example.InventoryService.repository.FlightRepository;
import com.example.InventoryService.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatServiceImpl implements SeatService {

    private final FlightRepository flightRepository;
    private final SeatRepository seatRepository;

    @Override
    public void createSeats(
            String flightId,
            CreateSeatsRequest request) {

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() ->
                        new FlightNotFoundException(
                                "Flight not found"
                        ));

        List<Seat> seats = new ArrayList<>();

        for(int row = 1; row <= request.getRows(); row++) {

            for(int col = 0;
                col < request.getSeatsPerRow();
                col++) {

                String seatNumber =
                        row + String.valueOf((char)('A' + col));

                seats.add(
                        Seat.builder()
                                .flight(flight)
                                .seatNumber(seatNumber)
                                .status(
                                        SeatStatus.AVAILABLE
                                )
                                .build()
                );
            }
        }

        seatRepository.saveAll(seats);
    }

    @Override
    public void reserveSeat(
            String flightId,
            String seatNumber) {

        Seat seat =
                seatRepository
                        .findByFlightIdAndSeatNumber(
                                flightId,
                                seatNumber
                        )
                        .orElseThrow(() ->
                                new SeatNotFoundException(
                                        "Seat not found"
                                ));

        if(seat.getStatus() != SeatStatus.AVAILABLE) {

            throw new SeatAlreadyBookedException(
                    "Seat already booked"
            );
        }

        seat.setStatus(SeatStatus.BOOKED);

        seatRepository.save(seat);
    }

    @Override
    public void releaseSeat(
            String flightId,
            String seatNumber) {

        Seat seat =
                seatRepository
                        .findByFlightIdAndSeatNumber(
                                flightId,
                                seatNumber
                        )
                        .orElseThrow(() ->
                                new SeatNotFoundException(
                                        "Seat not found"
                                ));

        seat.setStatus(SeatStatus.AVAILABLE);

        seatRepository.save(seat);
    }
}