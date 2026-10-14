package br.com.ifsp.tcc.app.repositories;

import java.util.Optional;

public interface CustomerRepository {

    record CustomerData(String customerId, String name, String billingAddressJson) {
    }

    Optional<CustomerData> findById(String customerId);
}
