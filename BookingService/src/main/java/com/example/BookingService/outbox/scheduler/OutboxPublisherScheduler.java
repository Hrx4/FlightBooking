package com.example.BookingService.outbox.scheduler;

import com.example.BookingService.kafka.producer.BookingProducer;
import com.example.BookingService.outbox.entity.OutboxEvent;
import com.example.BookingService.outbox.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingCreatedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final OutboxRepository outboxRepository;

    private final BookingProducer bookingProducer;

    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishEvents()
            throws Exception {

        List<OutboxEvent> events =
                outboxRepository
                        .findByProcessedFalse();

        for(OutboxEvent event : events) {

            try {
                if ("BOOKING_CREATED".equals(event.getEventType())) {
                    BookingCreatedEvent bookingEvent = objectMapper.readValue(
                            event.getPayload(), BookingCreatedEvent.class);

                    bookingProducer.publish(bookingEvent);

                    event.setProcessed(true);
                    outboxRepository.save(event);
                }
            } catch (Exception e) {
                System.err.println("Failed to publish outbox event " + event.getId() + ": " + e.getMessage());
            }
        }
    }
}
