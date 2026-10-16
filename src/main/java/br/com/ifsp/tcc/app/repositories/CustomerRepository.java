package br.com.ifsp.tcc.app.repositories;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {

    record Bill(boolean paid, double amount) {
    }

    record CustomerData(String customerId, String name, List<Bill> billingHistory) {
    }

    Optional<CustomerData> findById(String customerId);
}
