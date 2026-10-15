package br.com.ifsp.tcc;

import br.com.ifsp.tcc.app.factories.MakeBillingUseCase;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.infra.config.Configuration;
import br.com.ifsp.tcc.infra.database.DatabaseConfig;
import br.com.ifsp.tcc.infra.grpc.GrpcServer;
import br.com.ifsp.tcc.infra.http.HttpServer;

public final class Main {

    public static void main(String[] args) {
        try {
            DatabaseConfig.connect();

            BillingUseCase billingUseCase = MakeBillingUseCase.create();

            HttpServer httpServer = new HttpServer(billingUseCase);
            GrpcServer grpcServer = new GrpcServer(billingUseCase);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                grpcServer.stop();
                httpServer.stop();
                DatabaseConfig.close();
            }, "shutdown-hook"));

            httpServer.start(Configuration.HTTP_PORT);
            grpcServer.start(Configuration.GRPC_PORT);

        } catch (Exception e) {
            System.err.println("Error on trying to start server: " + e.getMessage());
            // e.printStackTrace();
            System.exit(1);
        }
    }
}