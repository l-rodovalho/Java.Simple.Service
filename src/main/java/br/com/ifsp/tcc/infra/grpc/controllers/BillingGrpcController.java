package br.com.ifsp.tcc.infra.grpc.controllers;

import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.grpc.BillingRequest;
import br.com.ifsp.tcc.grpc.BillingResponse;
import br.com.ifsp.tcc.grpc.BillingServiceGrpc;
import br.com.ifsp.tcc.grpc.Metrics;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public final class BillingGrpcController extends BillingServiceGrpc.BillingServiceImplBase {

    private final BillingUseCase billingUseCase;

    public BillingGrpcController(BillingUseCase billingUseCase) {
        this.billingUseCase = billingUseCase;
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
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }
}