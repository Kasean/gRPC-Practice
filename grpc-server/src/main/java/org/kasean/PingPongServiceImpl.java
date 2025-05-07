package org.kasean;

import io.grpc.stub.StreamObserver;
import pingpong.PingPongServiceGrpc;
import pingpong.PingRequest;
import pingpong.PongResponse;

public class PingPongServiceImpl extends PingPongServiceGrpc.PingPongServiceImplBase {

    @Override
    public void ping(PingRequest request, StreamObserver<PongResponse> responseObserver) {
        System.out.println("Received Ping: " + request.getMessage());

        PongResponse response = PongResponse.newBuilder()
                .setMessage("Pong to: " + request.getMessage())
                .setTimestamp(System.currentTimeMillis())
                .setId(request.getId())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
