package com.example.PaymentService.kafka.consumer;

import com.example.PaymentService.dto.RefundRequest;
import com.example.PaymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.example.sharedevents.BookingCancelledEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCancelledConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "booking-cancelled",
            groupId = "payment-group")
    public void consume(BookingCancelledEvent event) {

        RefundRequest refundRequest = new RefundRequest();

        refundRequest.setBookingId(event.getBookingId());

        paymentService.refundPayment(refundRequest);
    };
}
