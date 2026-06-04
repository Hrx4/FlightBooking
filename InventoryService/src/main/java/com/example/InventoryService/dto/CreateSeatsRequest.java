package com.example.InventoryService.dto;

import lombok.Data;

@Data
public class CreateSeatsRequest {

    private Integer rows;

    private Integer seatsPerRow;
}