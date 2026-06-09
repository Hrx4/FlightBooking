package com.example.BookingService.repository;

import com.example.BookingService.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, String> {

    List<Booking> findByUserId(String userId);
}