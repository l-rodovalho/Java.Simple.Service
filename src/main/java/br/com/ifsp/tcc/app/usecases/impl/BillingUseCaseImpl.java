package br.com.ifsp.tcc.app.usecases.impl;

import br.com.ifsp.tcc.app.repositories.CustomerRepository;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;

public final class BillingUseCaseImpl implements BillingUseCase {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final CustomerRepository customerRepository;

    public BillingUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public BillingResult execute(String customerId) {
        CustomerRepository.CustomerData customer = this.customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        JsonNode addressData;
        try {
            addressData = customer.billingAddressJson() != null
                    ? MAPPER.readTree(customer.billingAddressJson())
                    : null;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        JsonNode billsNode = addressData != null ? addressData.get("billingHistory") : null;
        int invoicesProcessed = 0;
        double totalPaid = 0.0;
        double totalDebt = 0.0;

        if (billsNode != null && billsNode.isArray()) {
            invoicesProcessed = billsNode.size();
            for (JsonNode bill : billsNode) {
                boolean paid = bill.path("paid").asBoolean(false);
                double amount = bill.path("amount").asDouble(0.0);

                if (paid) {
                    totalPaid += amount;
                    continue;
                }

                int daysOverdue = 30;
                double dailyRate = 0.0033;
                double totalDebtWithInterest = amount * Math.pow(1.0 + dailyRate, daysOverdue);

                totalDebt += Math.abs(totalDebtWithInterest);
            }
        }

        Metrics metrics = new Metrics(
                invoicesProcessed,
                String.format(Locale.US, "%.2f", totalPaid),
                String.format(Locale.US, "%.2f", totalDebt));

        return new BillingResult(
                customer.customerId(),
                customer.name(),
                metrics);
    }
}
