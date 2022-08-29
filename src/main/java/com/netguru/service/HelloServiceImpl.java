package com.netguru.service;

import com.netguru.grpc.HelloRequest;
import com.netguru.grpc.HelloResponse;
import com.netguru.grpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

    @Override
    public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println("Request received from client:\n" + request);

        String greeting = "Hello, " +
                request.getFirstName() +
                " " +
                request.getLastName();

        HelloResponse response = HelloResponse.newBuilder()
            .setGreeting(greeting)
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
