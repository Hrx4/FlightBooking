package com.example.BookingService.kafka.consumer;

import com.example.BookingService.entity.Booking;
import com.example.BookingService.entity.BookingStatus;
import com.example.BookingService.kafka.producer.BookingConfirmedProducer;
import com.example.BookingService.metrics.BookingMetrics;
import com.example.BookingService.repository.BookingRepository;
import com.example.BookingService.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingConfirmedEvent;
import org.example.sharedevents.PaymentSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final BookingRepository bookingRepository;
    private final BookingConfirmedProducer  bookingConfirmedProducer;
    private final BookingMetrics bookingMetrics;

    @KafkaListener(
            topics = "payment-success",
            groupId = "booking-group"
    )
    public void consume(
            PaymentSuccessEvent event) {

        Booking booking =
                bookingRepository
                        .findById(
                                event.getBookingId()
                        )
                        .orElseThrow();

        bookingConfirmedProducer.publish(
                BookingConfirmedEvent.builder()
                        .bookingId(
                                event.getBookingId())
                        .flightId(
                                event.getFlightId())
                        .seatNumber(
                                event.getSeatNumber())
                        .build()
        );
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        System.out.println(
                "Booking Confirmed"
        );
        bookingMetrics.bookingSuccess();
    }
}