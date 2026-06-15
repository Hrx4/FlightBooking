package com.example.BookingService.service;

import com.example.BookingService.dto.BookingResponse;
import com.example.BookingService.dto.CreateBookingRequest;
import com.example.BookingService.entity.Booking;
import com.example.BookingService.entity.BookingStatus;
import com.example.BookingService.grpc.InventoryGrpcClient;
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

    private final InventoryGrpcClient inventoryGrpcClient;


    private final BookingProducer bookingProducer;

    private final ObjectMapper objectMapper;

    private final OutboxRepository outboxRepository;




    @Override
    public BookingResponse createBooking(String userId, CreateBookingRequest request) {

        // 1. Idempotency check first
        System.out.println("check existing booking");
        Optional<Booking> existingBooking = bookingRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingBooking.isPresent()) {
            Booking existing = existingBooking.get();
            return BookingResponse.builder()
                    .bookingId(existing.getId())
                    .status(existing.getStatus())
                    .message("Duplicate request - booking already exists")
                    .build();
        }
        System.out.println("existingBooking" + existingBooking);

        // 2. Lock the seat via gRPC BEFORE creating the booking
        boolean locked = inventoryGrpcClient.lockSeat(request.getFlightId(), request.getSeatNumber());


        // 3. Now create the booking
        Booking booking = Booking.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .userId(userId)
                .flightId(request.getFlightId())
                .seatNumber(request.getSeatNumber())
                .amount(request.getAmount())
                .status(BookingStatus.PENDING)
                .build();

        bookingRepository.save(booking);

        if (!locked) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            return BookingResponse.builder()
                    .bookingId(booking.getId())
                    .status(BookingStatus.CANCELLED)
                    .message("Seat unavailable")
                    .build();
        }

        try {
            BookingCreatedEvent event = BookingCreatedEvent.builder()
                    .bookingId(booking.getId())
                    .flightId(booking.getFlightId())
                    .seatNumber(booking.getSeatNumber())
                    .userId(booking.getUserId())
                    .amount(booking.getAmount())
                    .build();

            String payload = objectMapper.writeValueAsString(event);

            outboxRepository.save(
                    OutboxEvent.builder()
                            .aggregateId(booking.getId())
                            .eventType("BOOKING_CREATED")
                            .payload(payload)
                            .processed(false)
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            return BookingResponse.builder()
                    .bookingId(booking.getId())
                    .status(BookingStatus.PENDING)
                    .message("Booking currently processing ....")
                    .build();

        } catch (Exception ex) {

            // 4. If outbox/Kafka setup fails, release the seat and cancel
            try {
                inventoryGrpcClient.releaseSeat(request.getFlightId(), request.getSeatNumber());
            } catch (Exception ignored) {}

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            throw new RuntimeException("Booking Failed", ex);
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
