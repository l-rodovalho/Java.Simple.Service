package br.com.ifsp.tcc.infra.http;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase;
import io.javalin.Javalin;

public final class HttpServer {

    private final Javalin app;

    public HttpServer(BillingUseCase billingUseCase, CalculateInvoicesUseCase calculateInvoicesUseCase) {
        this.app = Javalin.create(config -> {
            config.useVirtualThreads = true;
            config.showJavalinBanner = false;
            config.http.maxRequestSize = 16777216L;
        });

        Routes.register(this.app, billingUseCase, calculateInvoicesUseCase);
    }

    public void start(int port) {
        this.app.start(port);
        System.out.println("HTTP server listening on port " + port);
    }

    public void stop() {
        this.app.stop();
    }
}