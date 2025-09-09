package br.com.ukbank.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Domain-specific exceptions following proper error handling patterns
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateCustomerException extends RuntimeException {
    public DuplicateCustomerException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountOperationException extends RuntimeException {
    public AccountOperationException(String message) {
        super(message);
    }
}
