package br.com.ifsp.tcc.infra.database.repositories;

import br.com.ifsp.tcc.app.repositories.CustomerRepository;
import br.com.ifsp.tcc.infra.database.DatabaseConfig;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.Optional;
import java.util.UUID;

public final class CustomerRepositoryImpl implements CustomerRepository {

    @Override
    public Optional<CustomerData> findById(String customerId) {
        String sql = "SELECT cd_cliente, nome, endereco_cobranca FROM cliente WHERE cd_cliente = $1";

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

        Object billingAddressObj = row.getValue("endereco_cobranca");
        String billingAddressJson = billingAddressObj != null ? billingAddressObj.toString() : null;

        return Optional.of(new CustomerData(resultCustomerId, name, billingAddressJson));
    }
}