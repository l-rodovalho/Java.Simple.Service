package br.com.ifsp.tcc.infra.http;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase;
import br.com.ifsp.tcc.infra.http.controllers.BillingHttpController;
import br.com.ifsp.tcc.infra.http.controllers.CalculateInvoicesHttpController;
import br.com.ifsp.tcc.infra.http.controllers.HealthHttpController;
import io.javalin.Javalin;

public final class Routes {

    private Routes() {
        // Prevent instantiation
    }

    public static void register(Javalin app, BillingUseCase billingUseCase, CalculateInvoicesUseCase calculateInvoicesUseCase) {
        BillingHttpController billingController = new BillingHttpController(billingUseCase);
        CalculateInvoicesHttpController calculateInvoicesController = new CalculateInvoicesHttpController(calculateInvoicesUseCase);
        HealthHttpController healthController = new HealthHttpController();

        app.get("/health", healthController);
        app.post("/billing/{id}", billingController);
        app.post("/invoices/calculate", calculateInvoicesController);
    }
}