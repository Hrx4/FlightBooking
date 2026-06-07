package com.example.PaymentService.repository;


import com.example.PaymentService.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment,String> {

    Optional<Payment> findByBookingId(
            String bookingId
    );
}
