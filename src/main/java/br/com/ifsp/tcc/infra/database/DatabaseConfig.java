package br.com.ifsp.tcc.infra.database;

import br.com.ifsp.tcc.infra.config.Configuration;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

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

        pool = Pool.pool(connectOptions, poolOptions);

        System.out.println("Connected to PostgreSQL successfully on port " + Configuration.DB_PORT);
    }

    public static Pool getPool() {
        if (pool == null) {
            throw new IllegalStateException("Error connecting to PostgreSQL");
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
