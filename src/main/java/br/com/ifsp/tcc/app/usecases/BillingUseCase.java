package br.com.ifsp.tcc.app.usecases;

public interface BillingUseCase {

    record Metrics(
            int invoicesProcessed,
            String totalPaid,
            String totalDueWithInterest) {
    }

    record BillingResult(
            String customerId,
            String name,
            Metrics metrics) {
    }

    BillingResult execute(String customerId);
}
