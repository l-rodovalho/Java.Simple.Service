package br.com.ifsp.tcc.app.errors;

public final class AppErrors {

    private AppErrors() {
        // Prevent instantiation
    }

    public static final class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException() {
            super("Customer not found");
        }
    }

    public static final class InvalidCustomerIdException extends RuntimeException {
        public InvalidCustomerIdException() {
            super("Invalid customer_id");
        }
    }

    public static final class InvalidRequestException extends RuntimeException {
        public InvalidRequestException(String message) {
            super(message);
        }
    }
}

