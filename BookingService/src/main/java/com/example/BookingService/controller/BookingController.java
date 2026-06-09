package com.example.BookingService.controller;

import com.example.BookingService.dto.BookingResponse;
import com.example.BookingService.dto.CreateBookingRequest;
import com.example.BookingService.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse>
    createBooking(
            @RequestBody
            CreateBookingRequest request) {

        /*
         * Later extract from JWT
         */
        String userId = "user-123";

        return ResponseEntity.ok(
                bookingService.createBooking(
                        userId,
                        request
                )
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse>
    getBooking(
            @PathVariable String bookingId) {

        return ResponseEntity.ok(
                bookingService.getBooking(
                        bookingId
                )
        );
    }
}