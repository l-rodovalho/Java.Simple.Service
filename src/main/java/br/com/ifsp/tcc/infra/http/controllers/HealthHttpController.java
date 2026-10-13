package br.com.ifsp.tcc.infra.http.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public final class HealthHttpController implements Handler {

    public record HealthResponse(String message) {
    }

    private static final HealthResponse HEALTH_OK_RESPONSE = new HealthResponse("OK");

    @Override
    public void handle(Context ctx) {
        ctx.status(HttpStatus.OK).json(HEALTH_OK_RESPONSE);
    }
}
