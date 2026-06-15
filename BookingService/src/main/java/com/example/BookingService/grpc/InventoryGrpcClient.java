package com.example.BookingService.grpc;

import inventory.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryGrpcClient {

    private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;

    public InventoryGrpcClient(GrpcChannelFactory channelFactory) {
        this.inventoryStub = InventoryServiceGrpc.newBlockingStub(
                channelFactory.createChannel("inventory-service")
        );
    }

    @Retry(name = "inventoryRetry")
    @CircuitBreaker(name = "inventoryCircuit", fallbackMethod = "inventoryFallback")
    public boolean lockSeat(String flightId, String seatNumber) {
        LockSeatRequest request = LockSeatRequest.newBuilder()
                .setFlightId(flightId)
                .setSeatNumber(seatNumber)
                .build();

        LockSeatResponse response = inventoryStub.lockSeat(request);

        return response.getSuccess();
    }

    public void releaseSeat(String flightId, String seatNumber) {
        ReleaseSeatRequest request = ReleaseSeatRequest.newBuilder()
                .setFlightId(flightId)
                .setSeatNumber(seatNumber)
                .build();

        ReleaseSeatResponse response = inventoryStub.releaseSeat(request);
        if (!response.getSuccess()) {
            throw new RuntimeException("Seat release failed: " + response.getMessage());
        }
    }

    public boolean inventoryFallback(String flightId, String seatNumber, Exception ex) {
        System.out.println("Inventory service down"+ ex);
        return false; // caller checks this
    }
}