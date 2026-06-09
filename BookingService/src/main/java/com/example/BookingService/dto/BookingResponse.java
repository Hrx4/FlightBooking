package com.example.BookingService.dto;

import com.example.BookingService.entity.BookingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {

    private String bookingId;

    private BookingStatus status;

    private String message;
}