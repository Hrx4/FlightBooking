package com.example.BookingService.service;

import com.example.BookingService.dto.BookingResponse;
import com.example.BookingService.dto.CreateBookingRequest;

public interface BookingService {

    BookingResponse createBooking(
            String userId,
            CreateBookingRequest request
    );

    BookingResponse getBooking(
            String bookingId
    );
}