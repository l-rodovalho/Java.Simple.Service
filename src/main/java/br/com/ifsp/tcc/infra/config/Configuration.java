package br.com.ifsp.tcc.infra.config;

import io.github.cdimascio.dotenv.Dotenv;

public final class Configuration {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static final int HTTP_PORT;
    public static final int GRPC_PORT;
    public static final String DB_HOST;
    public static final int DB_PORT;
    public static final String DB_USER;
    public static final String DB_PASSWORD;
    public static final String DB_NAME;
    public static final int DB_POOL_MAX;

    static {
        HTTP_PORT = validateNumber("HTTP_PORT", getEnv("HTTP_PORT"));
        GRPC_PORT = validateNumber("GRPC_PORT", getEnv("GRPC_PORT"));
        DB_HOST = validateString("DB_HOST", getEnv("DB_HOST"));
        DB_PORT = validateNumber("DB_PORT", getEnv("DB_PORT"));
        DB_USER = validateString("DB_USER", getEnv("DB_USER"));
        DB_PASSWORD = validateString("DB_PASSWORD", getEnv("DB_PASSWORD"));
        DB_NAME = validateString("DB_NAME", getEnv("DB_NAME"));
        DB_POOL_MAX = validateNumber("DB_POOL_MAX", getEnv("DB_POOL_MAX"));
    }

    private Configuration() {
        // Prevent instantiation
    }

    private static String getEnv(String variable) {
        String value = dotenv.get(variable);
        if (value == null || value.isBlank()) {
            value = System.getenv(variable);
        }
        return value;
    }

    private static String validateString(String variable, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variable " + variable + " is required!");
        }
        return value;
    }

    private static int validateNumber(String variable, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variable " + variable + " is required!");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Variable " + variable + " must be a number!");
        }
    }
}
