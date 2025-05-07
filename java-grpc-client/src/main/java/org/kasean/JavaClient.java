package org.kasean;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pingpong.PingPongServiceGrpc;
import pingpong.PingRequest;
import pingpong.PongResponse;

public class JavaClient {
    public static void main(String[] args) throws InterruptedException {

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();


        PingPongServiceGrpc.PingPongServiceBlockingStub stub =
                PingPongServiceGrpc.newBlockingStub(channel);

        System.out.println("Start pings");

        for (int i = 0; i < 10; i++) {
            PingRequest request = PingRequest.newBuilder()
                    .setMessage("Ping " + i + " from Java client.")
                    .setTimestamp(System.currentTimeMillis())
                    .setId(i)
                    .build();


            PongResponse response = stub.ping(request);


            System.out.println("Response from server: " + response.getMessage());
            System.out.println("Timestamp: " + response.getTimestamp());
            System.out.println("ID: " + response.getId());

            Thread.sleep(5_000);
        }

        channel.shutdown();
    }
}