package br.com.ukbank.domain.model;

/**
 * Enumeration representing different types of bank accounts
 * Following UK banking standards
 */
public enum AccountType {
    CURRENT("Current Account"),
    SAVINGS("Savings Account"),
    ISA("Individual Savings Account"),
    JOINT("Joint Account"),
    BUSINESS("Business Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Business logic to determine if account type allows overdraft
     */
    public boolean allowsOverdraft() {
        return this == CURRENT || this == BUSINESS;
    }

    /**
     * Business logic to determine if account type has interest
     */
    public boolean hasInterest() {
        return this == SAVINGS || this == ISA;
    }
}
