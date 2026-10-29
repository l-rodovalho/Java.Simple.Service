package br.com.ifsp.tcc.infra.http.controllers;

import br.com.ifsp.tcc.app.errors.AppErrors.InvalidRequestException;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase.InvoiceInput;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public final class CalculateInvoicesHttpController implements Handler {

    private final CalculateInvoicesUseCase calculateInvoicesUseCase;

    public CalculateInvoicesHttpController(CalculateInvoicesUseCase calculateInvoicesUseCase) {
        this.calculateInvoicesUseCase = calculateInvoicesUseCase;
    }

    @Override
    public void handle(Context ctx) {
        RequestDto body = ctx.bodyAsClass(RequestDto.class);
        try {
            List<InvoiceInput> invoices = body == null ? null : toInputs(body.invoices());
            int rounds = body == null || body.rounds() == null ? 1 : body.rounds();

            CalculateInvoicesUseCase.Result result = this.calculateInvoicesUseCase.execute(invoices, rounds);

            ctx.status(HttpStatus.OK).json(new ResponseDto(
                    result.invoicesProcessed(),
                    result.totalPaid(),
                    result.totalDiscount(),
                    result.totalDueWithInterest()));
        } catch (InvalidRequestException e) {
            ctx.status(HttpStatus.BAD_REQUEST).json(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(Map.of("error", "Internal server error"));
        }
    }

    private static List<InvoiceInput> toInputs(List<InvoiceDto> dtos) {
        if (dtos == null) {
            return null;
        }

        List<InvoiceInput> invoices = new ArrayList<>(dtos.size());
        for (int i = 0; i < dtos.size(); i++) {
            InvoiceDto dto = dtos.get(i);
            if (dto == null) {
                invoices.add(null);
                continue;
            }
            if (dto.amount() == null) {
                throw new InvalidRequestException("invoices[" + i + "].amount must be a finite number >= 0");
            }
            if (dto.daysOverdue() == null) {
                throw new InvalidRequestException(
                        "invoices[" + i + "].days_overdue must be an integer between 0 and 365");
            }
            if (dto.paid() == null) {
                throw new InvalidRequestException("invoices[" + i + "].paid must be a boolean");
            }
            if (dto.tier() == null) {
                throw new InvalidRequestException("invoices[" + i + "].tier must be an integer between 0 and 3");
            }
            invoices.add(new InvoiceInput(dto.amount(), dto.daysOverdue(), dto.paid(), dto.tier()));
        }
        return invoices;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record RequestDto(
            @JsonProperty("invoices") List<InvoiceDto> invoices,
            @JsonProperty("rounds") Integer rounds) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record InvoiceDto(
            @JsonProperty("amount") Double amount,
            @JsonProperty("days_overdue") Integer daysOverdue,
            @JsonProperty("paid") Boolean paid,
            @JsonProperty("tier") Integer tier) {
    }

    @JsonPropertyOrder({ "invoices_processed", "total_paid", "total_discount", "total_due_with_interest" })
    private record ResponseDto(
            @JsonProperty("invoices_processed") int invoicesProcessed,
            @JsonProperty("total_paid") String totalPaid,
            @JsonProperty("total_discount") String totalDiscount,
            @JsonProperty("total_due_with_interest") String totalDueWithInterest) {
    }
}
