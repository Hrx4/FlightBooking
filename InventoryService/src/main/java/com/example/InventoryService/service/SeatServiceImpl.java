package com.example.InventoryService.service;

import com.example.InventoryService.dto.CreateSeatsRequest;
import com.example.InventoryService.entity.Flight;
import com.example.InventoryService.entity.Seat;
import com.example.InventoryService.entity.SeatStatus;
import com.example.InventoryService.exception.FlightNotFoundException;
import com.example.InventoryService.exception.SeatAlreadyBookedException;
import com.example.InventoryService.exception.SeatNotFoundException;
import com.example.InventoryService.exception.SeatReservationConflictException;
import com.example.InventoryService.repository.FlightRepository;
import com.example.InventoryService.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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

    @Transactional
    public void reserveSeat(
            String flightId,
            String seatNumber) {

        try {

            Seat seat =
                    seatRepository
                            .findByFlight_IdAndSeatNumber(
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

        } catch (ObjectOptimisticLockingFailureException ex) {

            throw new SeatReservationConflictException(
                    "Seat was booked by another user"
            );
        }
    }

    @Override
    @Transactional
    public void releaseSeat(
            String flightId,
            String seatNumber) {

        Seat seat = seatRepository
                .findByFlight_IdAndSeatNumber(
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

    @Override
    @Transactional
    public void lockSeat(
            String flightId,
            String seatNumber) {

        Seat seat = seatRepository
                .findByFlight_IdAndSeatNumber(
                        flightId,
                        seatNumber
                )
                .orElseThrow(() ->
                        new SeatNotFoundException(
                                "Seat not found"
                        ));

        if(seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatAlreadyBookedException(
                    "Seat unavailable"
            );
        }

        seat.setStatus(SeatStatus.LOCKED);

        seatRepository.save(seat);
    }

    @Override
    @Transactional
    public void confirmSeat(
            String flightId,
            String seatNumber) {

        Seat seat = seatRepository
                .findByFlight_IdAndSeatNumber(
                        flightId,
                        seatNumber
                )
                .orElseThrow(() ->
                        new SeatNotFoundException(
                                "Seat not found"
                        ));

        if(seat.getStatus() != SeatStatus.LOCKED) {

            throw new RuntimeException(
                    "Seat is not locked"
            );
        }

        seat.setStatus(SeatStatus.BOOKED);

        seatRepository.save(seat);
    }
}