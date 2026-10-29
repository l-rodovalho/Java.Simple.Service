package br.com.ifsp.tcc;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase;
import br.com.ifsp.tcc.app.usecases.impl.BillingUseCaseImpl;
import br.com.ifsp.tcc.infra.config.Configuration;
import br.com.ifsp.tcc.infra.database.DatabaseConfig;
import br.com.ifsp.tcc.infra.database.repositories.CustomerRepositoryImpl;
import br.com.ifsp.tcc.infra.grpc.GrpcServer;
import br.com.ifsp.tcc.infra.http.HttpServer;

public final class Main {

    private static HttpServer httpServer = null;
    private static GrpcServer grpcServer = null;

    public static void main(String[] args) {
        try {
            DatabaseConfig.connect();

            BillingUseCase billingUseCase = new BillingUseCaseImpl(new CustomerRepositoryImpl());
            CalculateInvoicesUseCase calculateInvoicesUseCase = new CalculateInvoicesUseCase(100000);

            String protocol = Configuration.PROTOCOL;

            if (protocol.equals("HTTP")) {
                httpServer = new HttpServer(billingUseCase, calculateInvoicesUseCase);
                httpServer.start(Configuration.HTTP_PORT);
            } else if (protocol.equals("GRPC")) {
                grpcServer = new GrpcServer(billingUseCase, calculateInvoicesUseCase);
                grpcServer.start(Configuration.GRPC_PORT);
            } else {
                throw new IllegalStateException("PROTOCOL must be HTTP or GRPC");
            }

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (grpcServer != null) {
                    grpcServer.stop();
                }
                if (httpServer != null) {
                    httpServer.stop();
                }
                DatabaseConfig.close();
            }, "shutdown-hook"));

        } catch (Exception e) {
            System.err.println("Error on trying to start server: " + e.getMessage());
            // e.printStackTrace();
            System.exit(1);
        }
    }
}