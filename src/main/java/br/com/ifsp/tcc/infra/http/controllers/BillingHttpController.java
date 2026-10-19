package br.com.ifsp.tcc.infra.http.controllers;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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

            BillingResponseDto responseDto = new BillingResponseDto(
                    result.customerId(),
                    result.name(),
                    new MetricsDto(
                            result.metrics().invoicesProcessed(),
                            result.metrics().totalPaid(),
                            result.metrics().totalDueWithInterest()));

            ctx.status(HttpStatus.OK).json(responseDto);
        } catch (IllegalArgumentException e) {
            ctx.status(HttpStatus.NOT_FOUND).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "Internal server error"));
        }
    }

    @JsonPropertyOrder({ "customer_id", "name", "metrics" })
    private record BillingResponseDto(
            @JsonProperty("customer_id") String customerId,
            @JsonProperty("name") String name,
            @JsonProperty("metrics") MetricsDto metrics) {
    }

    @JsonPropertyOrder({ "invoices_processed", "total_paid", "total_due_with_interest" })
    private record MetricsDto(
            @JsonProperty("invoices_processed") int invoicesProcessed,
            @JsonProperty("total_paid") String totalPaid,
            @JsonProperty("total_due_with_interest") String totalDueWithInterest) {
    }
}
