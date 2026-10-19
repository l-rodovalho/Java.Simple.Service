package br.com.ifsp.tcc;

import br.com.ifsp.tcc.app.factories.MakeBillingUseCase;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.infra.config.Configuration;
import br.com.ifsp.tcc.infra.database.DatabaseConfig;
import br.com.ifsp.tcc.infra.grpc.GrpcServer;
import br.com.ifsp.tcc.infra.http.HttpServer;

public final class Main {

    private static HttpServer httpServer = null;
    private static GrpcServer grpcServer = null;

    public static void main(String[] args) {
        try {
            DatabaseConfig.connect();

            BillingUseCase billingUseCase = MakeBillingUseCase.create();

            String protocol = Configuration.PROTOCOL;

            if (protocol.equals("HTTP")) {
                httpServer = new HttpServer(billingUseCase);
                httpServer.start(Configuration.HTTP_PORT);
            } else if (protocol.equals("GRPC")) {
                grpcServer = new GrpcServer(billingUseCase);
                grpcServer.start(Configuration.GRPC_PORT);
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