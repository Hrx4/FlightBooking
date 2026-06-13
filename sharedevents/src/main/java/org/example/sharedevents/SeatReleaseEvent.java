package org.example.sharedevents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatReleaseEvent {

    private String bookingId;

    private String flightId;

    private String seatNumber;
}