package com.example.BookingService.service;

import com.example.BookingService.client.InventoryClient;
import com.example.BookingService.client.PaymentClient;
import com.example.BookingService.client.dto.PaymentRequest;
import com.example.BookingService.client.dto.PaymentResponse;
import com.example.BookingService.dto.BookingResponse;
import com.example.BookingService.dto.CreateBookingRequest;
import com.example.BookingService.entity.Booking;
import com.example.BookingService.entity.BookingStatus;
import com.example.BookingService.kafka.producer.BookingProducer;
import com.example.BookingService.outbox.entity.OutboxEvent;
import com.example.BookingService.outbox.repository.OutboxRepository;
import com.example.BookingService.repository.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingCreatedEvent;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final InventoryClient inventoryClient;

    private final PaymentClient paymentClient;

    private final BookingProducer bookingProducer;

    private final ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;





    @Override
    public BookingResponse createBooking(
            String userId,
            CreateBookingRequest request) {

        Optional<Booking> existingBooking =
                bookingRepository
                        .findByIdempotencyKey(
                                request.getIdempotencyKey()
                        );

        Booking booking = Booking.builder()
                .idempotencyKey(
                        request.getIdempotencyKey()
                )
                .userId(userId)
                .flightId(request.getFlightId())
                .seatNumber(request.getSeatNumber())
                .amount(request.getAmount())
                .status(BookingStatus.PENDING)
                .build();

        bookingRepository.save(booking);

        try {



            BookingCreatedEvent event =
                    BookingCreatedEvent.builder()
                            .bookingId(
                                    booking.getId())
                            .flightId(
                                    booking.getFlightId())
                            .seatNumber(
                                    booking.getSeatNumber())
                            .userId(
                                    booking.getUserId())
                            .amount(
                                    booking.getAmount())
                            .build();

            String payload =
                    objectMapper
                            .writeValueAsString(
                                    event
                            );

            outboxRepository.save(
                    OutboxEvent.builder()
                            .aggregateId(
                                    booking.getId())
                            .eventType(
                                    "BOOKING_CREATED")
                            .payload(
                                    payload)
                            .processed(false)
                            .createdAt(
                                    LocalDateTime.now())
                            .build()
            );

            return BookingResponse.builder()
                    .bookingId(
                            booking.getId())
                    .status(
                            BookingStatus.PENDING)
                    .message(
                            "Booking currently processing ....")
                    .build();

        } catch (Exception ex) {

            try {

                inventoryClient.releaseSeat(
                        request.getFlightId(),
                        request.getSeatNumber()
                );

            } catch (Exception ignored) {
            }

            booking.setStatus(
                    BookingStatus.CANCELLED
            );

            bookingRepository.save(booking);

            throw new RuntimeException(
                    "Booking Failed",
                    ex
            );
        }
    }

    @Override
    public BookingResponse getBooking(
            String bookingId) {

        Booking booking =
                bookingRepository.findById(
                                bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Booking not found"
                                ));

        return BookingResponse.builder()
                .bookingId(
                        booking.getId())
                .status(
                        booking.getStatus())
                .message(
                        "Booking Found")
                .build();
    }

    @Override
    public List<BookingResponse> getBookings(String bookingId) {
        List<Booking> bookings = bookingRepository.findByUserId(
                bookingId);
        System.out.println("bookings : " + bookings);
        return bookings.stream()
                .map(booking -> BookingResponse.builder()
                        .bookingId(booking.getId())
                        .status(booking.getStatus())
                        .message("Booking Found")
                        .build())
                .collect(Collectors.toList());
    }


}
