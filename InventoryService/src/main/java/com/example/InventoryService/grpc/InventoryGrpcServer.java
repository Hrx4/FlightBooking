package com.example.InventoryService.grpc;
import com.example.InventoryService.service.SeatService;
import inventory.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class InventoryGrpcServer extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final SeatService seatService;


    @Override
    public void lockSeat(LockSeatRequest request , StreamObserver<LockSeatResponse> responseObserver) {
        try {
            System.out.println(" checking seat locked ");
            seatService.lockSeat(
                    request.getFlightId(),
                    request.getSeatNumber()
            );

            LockSeatResponse response =
                    LockSeatResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Seat Locked")
                            .build();
            System.out.println("Seat not locked , grpc approves ");

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            System.out.println(" seat locked , grpc not approved ");
            LockSeatResponse response =
                    LockSeatResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(e.getMessage())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void releaseSeat(ReleaseSeatRequest request, StreamObserver<ReleaseSeatResponse> responseObserver) {
        try{
            seatService.releaseSeat(request.getFlightId(), request.getSeatNumber());
            ReleaseSeatResponse response = ReleaseSeatResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Seat Released")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }catch (Exception e){
            ReleaseSeatResponse response =
                    ReleaseSeatResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(e.getMessage())
                            .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}