package com.example.InventoryService.grpc;
import inventory.InventoryServiceGrpc;
import inventory.LockSeatRequest;
import inventory.LockSeatResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {
    @Override
    public void lockSeat(LockSeatRequest request , StreamObserver<LockSeatResponse> responseObserver) {
        LockSeatResponse response =
                LockSeatResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Seat Locked")
                        .build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }
}
