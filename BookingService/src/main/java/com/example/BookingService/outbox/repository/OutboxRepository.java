package com.example.BookingService.outbox.repository;

import com.example.BookingService.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository
        extends JpaRepository<OutboxEvent,String> {

    List<OutboxEvent>
    findByProcessedFalse();
}