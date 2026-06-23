package com.example.BookingService.controller;

import com.example.BookingService.dto.BookingResponse;
import com.example.BookingService.dto.CreateBookingRequest;
import com.example.BookingService.entity.Booking;
import com.example.BookingService.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/my")
    public List<BookingResponse> myBookings(
            Authentication authentication) {

        String userId = authentication.getName();

        return bookingService.getBookings(userId);
    }

    @PostMapping
    public ResponseEntity<BookingResponse>
    createBooking(
            @RequestBody
            CreateBookingRequest request,
            Authentication authentication) {

        String userId = authentication.getName();

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

    @GetMapping("/userid/{userId}")
            public ResponseEntity<List<BookingResponse>>
            getBookings(@PathVariable String userId){
        return ResponseEntity.ok(
                bookingService.getBookings(userId)
        );
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable String bookingId,
            Authentication authentication) {

        String userId =
                authentication.getName();

        bookingService.cancelBooking(
                bookingId,
                userId);

        return ResponseEntity.ok().build();
    }
}