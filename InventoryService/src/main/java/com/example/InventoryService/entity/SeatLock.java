package com.example.InventoryService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class SeatLock {

    @Id
    private String id;

    private String seatId;

    private String bookingId;

    private LocalDateTime expiresAt;
}
