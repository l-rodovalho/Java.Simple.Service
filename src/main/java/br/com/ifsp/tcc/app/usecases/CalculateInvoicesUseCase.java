package br.com.ifsp.tcc.app.usecases;

import br.com.ifsp.tcc.app.errors.AppErrors.InvalidRequestException;

import java.util.List;
import java.util.Locale;

public final class CalculateInvoicesUseCase {

    public record InvoiceInput(double amount, int daysOverdue, boolean paid, int tier) {
    }

    public record Result(
            int invoicesProcessed,
            String totalPaid,
            String totalDiscount,
            String totalDueWithInterest) {
    }

    private static final double BASE_DAILY_RATE = 0.0033;
    private static final double[] TIER_DISCOUNT = { 0.0, 0.02, 0.05, 0.10 };
    private static final double[] TIER_RATE_MULT = { 1.0, 0.9, 0.75, 0.6 };

    private final int maxInvoices;

    public CalculateInvoicesUseCase(int maxInvoices) {
        this.maxInvoices = maxInvoices;
    }

    public Result execute(List<InvoiceInput> invoices, int rounds) {
        if (invoices == null || invoices.size() > this.maxInvoices) {
            throw new InvalidRequestException("invoices must be an array with at most " + this.maxInvoices + " items");
        }
        if (rounds < 1 || rounds > 1000) {
            throw new InvalidRequestException("rounds must be an integer between 1 and 1000");
        }

        double totalPaid = 0.0;
        double totalDiscount = 0.0;
        double totalDue = 0.0;

        for (int i = 0; i < invoices.size(); i++) {
            InvoiceInput inv = invoices.get(i);
            if (inv == null) {
                throw new InvalidRequestException("invoices[" + i + "] must be an object");
            }
            double amount = inv.amount();
            int days = inv.daysOverdue();
            int tier = inv.tier();
            if (!Double.isFinite(amount) || amount < 0) {
                throw new InvalidRequestException("invoices[" + i + "].amount must be a finite number >= 0");
            }
            if (days < 0 || days > 365) {
                throw new InvalidRequestException(
                        "invoices[" + i + "].days_overdue must be an integer between 0 and 365");
            }
            if (tier < 0 || tier > 3) {
                throw new InvalidRequestException("invoices[" + i + "].tier must be an integer between 0 and 3");
            }

            if (inv.paid()) {
                double discount = amount * TIER_DISCOUNT[tier];
                totalDiscount += discount;
                totalPaid += amount - discount;
                continue;
            }

            double rate = BASE_DAILY_RATE * TIER_RATE_MULT[tier] * (1.0 + Math.log1p(amount / 1000.0));
            double acc = 0.0;
            for (int r = 0; r < rounds; r++) {
                int d = days + (r % 7);
                double factor = Math.pow(1.0 + rate, d);
                double decay = Math.exp(-d / 90.0);
                double interest = amount * (factor - 1.0) * (1.0 + 0.5 * decay);
                double penalty = d > 30 ? amount * 0.02 : 0.0;
                acc += amount + interest + penalty;
            }
            totalDue += acc / rounds;
        }

        return new Result(
                invoices.size(),
                String.format(Locale.US, "%.2f", totalPaid),
                String.format(Locale.US, "%.2f", totalDiscount),
                String.format(Locale.US, "%.2f", totalDue));
    }
}
