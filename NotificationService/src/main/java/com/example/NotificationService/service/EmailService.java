package com.example.NotificationService.service;

import lombok.extern.slf4j.Slf4j;
import org.example.sharedevents.BookingConfirmedEvent;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendBookingConfirmation(
            BookingConfirmedEvent event) {

        log.info(
                """
                ==================================
                BOOKING CONFIRMED
                Booking ID: {}
                Flight: {}
                Seat: {}
                ==================================
                """,
                event.getBookingId(),
                event.getFlightId(),
                event.getSeatNumber()
        );
    }
}
