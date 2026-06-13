package com.example.BookingService.kafka.consumer;

import com.example.BookingService.entity.Booking;
import com.example.BookingService.entity.BookingStatus;
import com.example.BookingService.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.PaymentFailedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final BookingRepository bookingRepository;

    @KafkaListener(
            topics = "payment-failed",
            groupId = "booking-group"
    )
    public void consume(
            PaymentFailedEvent event) {

        Booking booking =
                bookingRepository
                        .findById(
                                event.getBookingId()
                        )
                        .orElseThrow();

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        bookingRepository.save(booking);

        System.out.println(
                "Booking Cancelled"
        );
    }
}