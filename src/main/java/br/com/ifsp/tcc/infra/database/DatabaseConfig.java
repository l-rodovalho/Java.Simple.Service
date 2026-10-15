package br.com.ifsp.tcc.infra.database;

import br.com.ifsp.tcc.infra.config.Configuration;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;

public final class DatabaseConfig {

    private static Pool pool;

    private DatabaseConfig() {
        // Prevent instantiation
    }

    public static void connect() {
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setHost(Configuration.DB_HOST)
                .setPort(Configuration.DB_PORT)
                .setUser(Configuration.DB_USER)
                .setPassword(Configuration.DB_PASSWORD)
                .setDatabase(Configuration.DB_NAME);

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(Configuration.DB_POOL_MAX);

        pool = PgBuilder
                .pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .build();

        try {
            SqlConnection connection = pool.getConnection()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .join();

            System.out.println("Connected to PostgreSQL successfully on port " + Configuration.DB_PORT);

            connection.close().toCompletionStage().toCompletableFuture().join();
        } catch (Exception e) {
            close();
            throw new RuntimeException("Error connecting to PostgreSQL on port " + Configuration.DB_PORT, e);
        }
    }

    public static Pool getPool() {
        if (pool == null) {
            throw new IllegalStateException("Database pool has not been initialized. Call connect() first.");
        }
        return pool;
    }

    public static void close() {
        if (pool != null) {
            pool.close();
            pool = null;
        }
    }
}