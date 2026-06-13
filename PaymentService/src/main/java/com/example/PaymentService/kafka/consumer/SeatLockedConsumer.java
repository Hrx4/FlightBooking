package com.example.PaymentService.kafka.consumer;

import com.example.PaymentService.dto.PaymentRequest;
import com.example.PaymentService.dto.PaymentResponse;
import com.example.PaymentService.entity.PaymentStatus;
import org.example.sharedevents.PaymentSuccessEvent;
import com.example.PaymentService.kafka.producer.PaymentFailedProducer;
import com.example.PaymentService.kafka.producer.PaymentSuccessProducer;
import com.example.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.PaymentFailedEvent;
import org.example.sharedevents.SeatLockedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeatLockedConsumer {

    private final PaymentService paymentService;

    private final PaymentSuccessProducer successProducer;

    private final PaymentFailedProducer failedProducer;

    @KafkaListener(
            topics = "seat-locked",
            groupId = "payment-group"
    )
    public void consume(
            SeatLockedEvent event) {

            PaymentRequest request =
                    new PaymentRequest();

            request.setBookingId(
                    event.getBookingId()
            );

            request.setUserId(
                    event.getUserId()
            );

            request.setAmount(
                    event.getAmount()
            );

            PaymentResponse response =
                    paymentService.processPayment(
                            request
                    );


            if(response.getStatus() == PaymentStatus.SUCCESS) {
                successProducer.publish(
                        PaymentSuccessEvent.builder()
                                .bookingId(event.getBookingId())
                                .flightId(event.getFlightId())
                                .seatNumber(event.getSeatNumber())
                                .userId(event.getUserId())
                                .amount(event.getAmount())
                                .paymentId(response.getPaymentId())
                                .build()
                );
            }else{
                failedProducer.publish(
                        PaymentFailedEvent.builder()
                                .bookingId(event.getBookingId())
                                .flightId(event.getFlightId())
                                .seatNumber(event.getSeatNumber())
                                .userId(event.getUserId())
                                .amount(event.getAmount())
                                .reason("Payment failed")
                                .build()
                );
            }

    }
}
