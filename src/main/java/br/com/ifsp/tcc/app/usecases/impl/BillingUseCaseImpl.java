package br.com.ifsp.tcc.app.usecases.impl;

import br.com.ifsp.tcc.app.errors.AppErrors.CustomerNotFoundException;
import br.com.ifsp.tcc.app.errors.AppErrors.InvalidCustomerIdException;
import br.com.ifsp.tcc.app.repositories.CustomerRepository;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class BillingUseCaseImpl implements BillingUseCase {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    private final CustomerRepository customerRepository;

    public BillingUseCaseImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public BillingResult execute(String customerId) {
        if (customerId == null || !UUID_PATTERN.matcher(customerId).matches()) {
            throw new InvalidCustomerIdException();
        }

        CustomerRepository.CustomerData customer = this.customerRepository.findById(customerId)
                .orElseThrow(CustomerNotFoundException::new);

        List<CustomerRepository.Bill> billingHistory = customer.billingHistory();
        if (billingHistory == null) {
            billingHistory = Collections.emptyList();
        }

        int invoicesProcessed = billingHistory.size();
        double totalPaid = 0.0;
        double totalDebt = 0.0;

        for (CustomerRepository.Bill bill : billingHistory) {
            boolean paid = bill.paid();
            double amount = bill.amount();

            if (paid) {
                totalPaid += amount;
                continue;
            }

            int daysOverdue = 30;
            double dailyRate = 0.0033;
            double totalDebtWithInterest = amount * Math.pow(1.0 + dailyRate, daysOverdue);

            totalDebt += Math.abs(totalDebtWithInterest);
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
