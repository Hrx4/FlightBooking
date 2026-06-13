package org.example.sharedevents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSuccessEvent {

    private String bookingId;

    private String flightId;

    private String seatNumber;

    private String userId;

    private BigDecimal amount;

    private String paymentId;
}
