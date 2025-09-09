package br.com.ukbank.domain.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Value object representing UK sort codes and account numbers
 * Immutable and self-validating following UK banking standards
 */
@Embeddable
@Getter
@EqualsAndHashCode
public class AccountIdentifier {

    private static final Pattern SORT_CODE_PATTERN = Pattern.compile("^[0-9]{2}-[0-9]{2}-[0-9]{2}$");
    private static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[0-9]{8}$");

    private String sortCode;
    private String accountNumber;

    // Default constructor for JPA
    protected AccountIdentifier() {}

    private AccountIdentifier(String sortCode, String accountNumber) {
        this.sortCode = validateSortCode(sortCode);
        this.accountNumber = validateAccountNumber(accountNumber);
    }

    public static AccountIdentifier ukBankAccount(String accountNumber) {
        // Generate standard UK bank sort code (for demo purposes)
        return new AccountIdentifier("12-34-56", accountNumber);
    }

    public static AccountIdentifier of(String sortCode, String accountNumber) {
        return new AccountIdentifier(sortCode, accountNumber);
    }

    private String validateSortCode(String sortCode) {
        if (sortCode == null || !SORT_CODE_PATTERN.matcher(sortCode).matches()) {
            throw new IllegalArgumentException("Invalid UK sort code format. Expected: XX-XX-XX");
        }
        return sortCode;
    }

    private String validateAccountNumber(String accountNumber) {
        if (accountNumber == null || !ACCOUNT_NUMBER_PATTERN.matcher(accountNumber).matches()) {
            throw new IllegalArgumentException("Invalid UK account number format. Expected: 8 digits");
        }
        return accountNumber;
    }

    @Override
    public String toString() {
        return sortCode + " " + accountNumber;
    }
}
