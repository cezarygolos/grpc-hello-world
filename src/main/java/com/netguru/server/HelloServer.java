package com.netguru.server;

import java.io.IOException;

import com.netguru.service.HelloServiceImpl;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

public class HelloServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
            .addService(ProtoReflectionService.newInstance())
            .addService(new HelloServiceImpl()).build();

        System.out.println("Starting server...");
        server.start();
        System.out.println("Server started!");
        server.awaitTermination();
    }
}
