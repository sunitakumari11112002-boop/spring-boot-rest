package br.com.ukbank.domain.valueobjects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * Value Object representing UK bank account identifiers
 * Encapsulates account number and sort code with UK banking validation
 */
public final class AccountIdentifier {

    @NotBlank
    @Pattern(regexp = "^[0-9]{8}$", message = "Account number must be 8 digits")
    private final String accountNumber;

    @NotBlank
    @Pattern(regexp = "^[0-9]{2}-[0-9]{2}-[0-9]{2}$", message = "Sort code must be in format XX-XX-XX")
    private final String sortCode;

    private AccountIdentifier(String accountNumber, String sortCode) {
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
    }

    public static AccountIdentifier of(String accountNumber, String sortCode) {
        validateAccountNumber(accountNumber);
        validateSortCode(sortCode);
        return new AccountIdentifier(accountNumber, sortCode);
    }

    public static AccountIdentifier ukBankAccount(String accountNumber) {
        return new AccountIdentifier(accountNumber, "40-00-01");
    }

    private static void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || !accountNumber.matches("^[0-9]{8}$")) {
            throw new IllegalArgumentException("Invalid account number format");
        }
    }

    private static void validateSortCode(String sortCode) {
        if (sortCode == null || !sortCode.matches("^[0-9]{2}-[0-9]{2}-[0-9]{2}$")) {
            throw new IllegalArgumentException("Invalid sort code format");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getSortCode() {
        return sortCode;
    }

    public String getFormattedIdentifier() {
        return String.format("%s %s", sortCode, accountNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AccountIdentifier that = (AccountIdentifier) obj;
        return Objects.equals(accountNumber, that.accountNumber) &&
               Objects.equals(sortCode, that.sortCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber, sortCode);
    }

    @Override
    public String toString() {
        return getFormattedIdentifier();
    }
}
