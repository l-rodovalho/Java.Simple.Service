package br.com.ifsp.tcc.infra.database.repositories;

import br.com.ifsp.tcc.app.repositories.CustomerRepository;
import br.com.ifsp.tcc.infra.database.DatabaseConfig;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public Optional<CustomerData> findById(String customerId) {
        String sql = "SELECT cd_cliente, nome, endereco_cobranca FROM cliente WHERE cd_cliente = $1 AND ativo = true";

        UUID uuidParam;
        try {
            uuidParam = UUID.fromString(customerId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        RowSet<Row> rows = DatabaseConfig.getPool()
                .preparedQuery(sql)
                .execute(Tuple.of(uuidParam))
                .toCompletionStage()
                .toCompletableFuture()
                .join();

        if (!rows.iterator().hasNext()) {
            return Optional.empty();
        }

        Row row = rows.iterator().next();

        String resultCustomerId = row.getUUID("cd_cliente").toString();
        String name = row.getString("nome");

        JsonObject billingAddress = row.getJsonObject("endereco_cobranca");
        List<CustomerRepository.Bill> bills = new ArrayList<>();

        if (billingAddress != null && billingAddress.containsKey("billingHistory")) {
            JsonArray billingHistory = billingAddress.getJsonArray("billingHistory");
            if (billingHistory != null) {
                for (int i = 0; i < billingHistory.size(); i++) {
                    JsonObject billJson = billingHistory.getJsonObject(i);
                    if (billJson != null) {
                        boolean paid = billJson.getBoolean("paid", false);
                        double amount = billJson.getDouble("amount", 0.0);
                        bills.add(new CustomerRepository.Bill(paid, amount));
                    }
                }
            }
        }

        return Optional.of(new CustomerData(resultCustomerId, name, bills));
    }
}