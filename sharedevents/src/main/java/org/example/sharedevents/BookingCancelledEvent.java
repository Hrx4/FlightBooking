package org.example.sharedevents;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent {

    private String bookingId;
    private String userId;
    private String flightId;
    private String seatNumber;
    private BigDecimal amount;
}