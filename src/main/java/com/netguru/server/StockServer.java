package com.netguru.server;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class StockServer {

    private final int port;
    private final Server server;

    public static void main(String[] args) throws Exception {
        StockServer stockServer = new StockServer(8980);
        stockServer.start();
        if (stockServer.server != null) {
            stockServer.server.awaitTermination();
        }
    }

    private StockServer(int port) {
        this.port = port;
        server = ServerBuilder.forPort(port)
            .addService(ProtoReflectionService.newInstance())
            .addService((BindableService) new StockService())
            .build();
    }

    private void start() throws IOException {
        server.start();
        System.out.println("Server started, listening on " + port);
        Runtime.getRuntime()
            .addShutdownHook(new Thread(() -> {
                System.err.println("shutting down server");
                try {
                    StockServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("server shutted down");
            }));
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown()
                .awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private static class StockService extends StockQuoteProviderGrpc.StockQuoteProviderImplBase {

        StockService() {
        }

        @Override
        public void serverSideStreamingGetListStockQuotes(Stock request, StreamObserver<StockQuote> responseObserver) {
            for (int i = 1; i <= 5; i++) {
                StockQuote stockQuote = StockQuote.newBuilder()
                    .setPrice(fetchStockPriceBid(request))
                    .setOfferNumber(i)
                    .setDescription("Price for stock:" + request.getTickerSymbol())
                    .build();
                responseObserver.onNext(stockQuote);
            }
            responseObserver.onCompleted();
        }

        @Override
        public StreamObserver<Stock> clientSideStreamingGetStatisticsOfStocks(final StreamObserver<StockQuote> responseObserver) {
            return new StreamObserver<>() {
                int count;
                double price = 0.0;
                StringBuffer sb = new StringBuffer();

                @Override
                public void onNext(Stock stock) {
                    count++;
                    price = +fetchStockPriceBid(stock);
                    sb.append(":")
                        .append(stock.getTickerSymbol());
                }

                @Override
                public void onCompleted() {
                    responseObserver.onNext(StockQuote.newBuilder()
                        .setPrice(price / count)
                        .setDescription("Statistics-" + sb.toString())
                        .build());
                    responseObserver.onCompleted();
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("error: " + t.getMessage());
                }
            };
        }

        @Override
        public StreamObserver<Stock> bidirectionalStreamingGetListsStockQuotes(final StreamObserver<StockQuote> responseObserver) {
            return new StreamObserver<>() {
                @Override
                public void onNext(Stock request) {
                    for (int i = 1; i <= 5; i++) {
                        StockQuote stockQuote = StockQuote.newBuilder()
                            .setPrice(fetchStockPriceBid(request))
                            .setOfferNumber(i)
                            .setDescription("Price for stock:" + request.getTickerSymbol())
                            .build();
                        responseObserver.onNext(stockQuote);
                    }
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }

                @Override
                public void onError(Throwable t) {
                    System.out.println("error: " + t.getMessage());
                }
            };
        }
    }

    private static double fetchStockPriceBid(Stock stock) {
        return stock.getTickerSymbol()
            .length()
            + ThreadLocalRandom.current()
                .nextDouble(-0.1d, 0.1d);
    }
}
