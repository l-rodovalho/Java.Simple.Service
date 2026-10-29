package br.com.ifsp.tcc.infra.grpc;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase;
import br.com.ifsp.tcc.infra.grpc.controllers.HealthGrpcController;
import br.com.ifsp.tcc.infra.grpc.controllers.BillingGrpcController;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class GrpcServer {

    private final BillingUseCase billingUseCase;
    private final CalculateInvoicesUseCase calculateInvoicesUseCase;
    private Server server;

    public GrpcServer(BillingUseCase billingUseCase, CalculateInvoicesUseCase calculateInvoicesUseCase) {
        this.billingUseCase = billingUseCase;
        this.calculateInvoicesUseCase = calculateInvoicesUseCase;
    }

    public void start(int port) {
        try {
            this.server = ServerBuilder.forPort(port)
                    .executor(Executors.newVirtualThreadPerTaskExecutor())
                    .maxInboundMessageSize(16777216)
                    .addService(new HealthGrpcController())
                    .addService(new BillingGrpcController(this.billingUseCase, this.calculateInvoicesUseCase))
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
