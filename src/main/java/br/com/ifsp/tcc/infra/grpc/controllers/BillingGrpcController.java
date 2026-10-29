package br.com.ifsp.tcc.infra.grpc.controllers;

import br.com.ifsp.tcc.app.errors.AppErrors.CustomerNotFoundException;
import br.com.ifsp.tcc.app.errors.AppErrors.InvalidCustomerIdException;
import br.com.ifsp.tcc.app.errors.AppErrors.InvalidRequestException;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.CalculateInvoicesUseCase;
import br.com.ifsp.tcc.grpc.BillingRequest;
import br.com.ifsp.tcc.grpc.BillingResponse;
import br.com.ifsp.tcc.grpc.BillingServiceGrpc;
import br.com.ifsp.tcc.grpc.CalculateInvoicesRequest;
import br.com.ifsp.tcc.grpc.CalculateInvoicesResponse;
import br.com.ifsp.tcc.grpc.Invoice;
import br.com.ifsp.tcc.grpc.Metrics;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public final class BillingGrpcController extends BillingServiceGrpc.BillingServiceImplBase {

    private final BillingUseCase billingUseCase;
    private final CalculateInvoicesUseCase calculateInvoicesUseCase;

    public BillingGrpcController(BillingUseCase billingUseCase, CalculateInvoicesUseCase calculateInvoicesUseCase) {
        this.billingUseCase = billingUseCase;
        this.calculateInvoicesUseCase = calculateInvoicesUseCase;
    }

    @Override
    public void processBilling(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
        try {
            String customerId = request.getCustomerId();
            BillingUseCase.BillingResult result = this.billingUseCase.execute(customerId);

            BillingResponse response = BillingResponse.newBuilder()
                    .setCustomerId(result.customerId())
                    .setName(result.name())
                    .setMetrics(Metrics.newBuilder()
                            .setInvoicesProcessed(result.metrics().invoicesProcessed())
                            .setTotalPaid(result.metrics().totalPaid())
                            .setTotalDueWithInterest(result.metrics().totalDueWithInterest())
                            .build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (InvalidCustomerIdException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (CustomerNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void calculateInvoices(CalculateInvoicesRequest request,
            StreamObserver<CalculateInvoicesResponse> responseObserver) {
        try {
            List<Invoice> protoInvoices = request.getInvoicesList();
            List<CalculateInvoicesUseCase.InvoiceInput> invoices = new ArrayList<>(protoInvoices.size());
            for (int i = 0; i < protoInvoices.size(); i++) {
                Invoice inv = protoInvoices.get(i);
                invoices.add(new CalculateInvoicesUseCase.InvoiceInput(
                        inv.getAmount(), inv.getDaysOverdue(), inv.getPaid(), inv.getTier()));
            }
            int rounds = request.getRounds() == 0 ? 1 : request.getRounds();

            CalculateInvoicesUseCase.Result result = this.calculateInvoicesUseCase.execute(invoices, rounds);

            CalculateInvoicesResponse response = CalculateInvoicesResponse.newBuilder()
                    .setInvoicesProcessed(result.invoicesProcessed())
                    .setTotalPaid(result.totalPaid())
                    .setTotalDiscount(result.totalDiscount())
                    .setTotalDueWithInterest(result.totalDueWithInterest())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (InvalidRequestException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }
}