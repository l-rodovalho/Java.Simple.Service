package br.com.ifsp.tcc.app.factories;

import br.com.ifsp.tcc.app.repositories.CustomerRepository;
import br.com.ifsp.tcc.app.usecases.BillingUseCase;
import br.com.ifsp.tcc.app.usecases.impl.BillingUseCaseImpl;
import br.com.ifsp.tcc.infra.database.repositories.CustomerRepositoryImpl;

public final class MakeBillingUseCase {

    private MakeBillingUseCase() {
        // Prevent instantiation
    }

    public static BillingUseCase create() {
        CustomerRepository customerRepository = new CustomerRepositoryImpl();
        return new BillingUseCaseImpl(customerRepository);
    }
}