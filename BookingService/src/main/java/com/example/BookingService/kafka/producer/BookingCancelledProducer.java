package com.example.BookingService.kafka.producer;


import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingCancelledEvent;
import org.example.sharedevents.BookingConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingCancelledProducer{

    private final KafkaTemplate<
            String,
            BookingCancelledEvent> kafkaTemplate;

    public void publish(
        BookingCancelledEvent event) {

    kafkaTemplate.send(
            "booking-cancelled",
            event);
}}