package br.com.ifsp.tcc.infra.grpc;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.infra.grpc.controllers.HealthGrpcController;
import br.com.ifsp.tcc.infra.grpc.controllers.BillingGrpcController;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class GrpcServer {

    private final BillingUseCase billingUseCase;
    private Server server;

    public GrpcServer(BillingUseCase billingUseCase) {
        this.billingUseCase = billingUseCase;
    }

    public void start(int port) {
        try {
            this.server = ServerBuilder.forPort(port)
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    .addService(new HealthGrpcController())
                    .addService(new BillingGrpcController(this.billingUseCase))
                    .build()
                    .start();

            System.out.println("gRPC server listening on port " + port);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start gRPC server on port " + port, e);
        }
    }

    public void stop() {
        if (this.server != null) {
            this.server.shutdown();
            try {
                if (!this.server.awaitTermination(5, TimeUnit.SECONDS)) {
                    this.server.shutdownNow();
                }
            } catch (InterruptedException e) {
                this.server.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
