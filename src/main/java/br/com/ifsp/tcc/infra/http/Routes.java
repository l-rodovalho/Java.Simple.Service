package br.com.ifsp.tcc.infra.http;

import br.com.ifsp.tcc.app.repositories.CustomerRepository;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.impl.BillingUseCaseImpl;
import br.com.ifsp.tcc.infra.database.repositories.CustomerRepositoryImpl;
import br.com.ifsp.tcc.infra.http.controllers.BillingHttpController;
import br.com.ifsp.tcc.infra.http.controllers.HealthHttpController;
import io.javalin.Javalin;

public final class Routes {

    private Routes() {
        // Prevent instantiation
    }

    public static void register(Javalin app) {
        CustomerRepository customerRepository = new CustomerRepositoryImpl();
        BillingUseCase billingUseCase = new BillingUseCaseImpl(customerRepository);

        BillingHttpController billingController = new BillingHttpController(billingUseCase);
        HealthHttpController healthController = new HealthHttpController();

        app.get("/health", healthController);
        app.post("/billing/{id}", billingController);
    }
}