package com.netguru.client;



import com.netguru.grpc.HelloRequest;
import com.netguru.grpc.HelloResponse;
import com.netguru.grpc.HelloServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class HelloClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
            .usePlaintext()
            .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub = HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
            .setFirstName("Johnny")
            .setLastName("Smith")
            .build());

        System.out.println("Response received from server:\n" + helloResponse);

        channel.shutdown();
    }
}
