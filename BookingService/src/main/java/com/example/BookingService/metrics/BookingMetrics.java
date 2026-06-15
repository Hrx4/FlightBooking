package com.example.BookingService.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingMetrics {

    private final MeterRegistry meterRegistry;

    public void bookingSuccess() {

        meterRegistry.counter(
                "booking.success"
        ).increment();
    }

    public void bookingFailure() {

        meterRegistry.counter(
                "booking.failed"
        ).increment();
    }
}