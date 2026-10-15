package br.com.ifsp.tcc.infra.grpc.controllers;

import br.com.ifsp.tcc.grpc.HealthRequest;
import br.com.ifsp.tcc.grpc.HealthResponse;
import br.com.ifsp.tcc.grpc.HealthServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public final class HealthGrpcController extends HealthServiceGrpc.HealthServiceImplBase {

    private static final HealthResponse HEALTH_OK_RESPONSE = HealthResponse.newBuilder()
            .setMessage("OK")
            .build();

    @Override
    public void check(HealthRequest request, StreamObserver<HealthResponse> responseObserver) {
        try {
            responseObserver.onNext(HEALTH_OK_RESPONSE);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }
}
