package br.com.ifsp.tcc.infra.http.controllers;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;

public final class BillingHttpController implements Handler {

    private final BillingUseCase billingUseCase;

    public BillingHttpController(BillingUseCase billingUseCase) {
        this.billingUseCase = billingUseCase;
    }

    @Override
    public void handle(Context ctx) {
        String customerId = ctx.pathParam("id");
        try {
            BillingUseCase.BillingResult result = this.billingUseCase.execute(customerId);
            ctx.status(HttpStatus.OK).json(result);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "Internal server error"));
        }
    }
}
