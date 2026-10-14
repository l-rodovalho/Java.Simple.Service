package br.com.ifsp.tcc.app.usecases;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface BillingUseCase {

    record Metrics(
            @JsonProperty("invoices_processed") int invoicesProcessed,
            @JsonProperty("total_paid") String totalPaid,
            @JsonProperty("total_due_with_interest") String totalDueWithInterest) {
    }

    record BillingResult(
            @JsonProperty("customer_id") String customerId,
            String name,
            Metrics metrics) {
    }

    BillingResult execute(String customerId);
}
