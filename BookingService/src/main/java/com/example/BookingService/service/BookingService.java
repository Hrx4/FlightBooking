package com.example.BookingService.service;

import com.example.BookingService.dto.BookingResponse;
import com.example.BookingService.dto.CreateBookingRequest;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(
            String userId,
            CreateBookingRequest request
    );

    BookingResponse getBooking(
            String bookingId
    );

    List<BookingResponse> getBookings(
             String bookingId
    );

    void cancelBooking(
            String bookingId,
            String userId
    );


}