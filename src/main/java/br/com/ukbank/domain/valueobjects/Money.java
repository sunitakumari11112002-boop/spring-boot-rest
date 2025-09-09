package br.com.ukbank.domain.valueobjects;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object representing monetary amounts in GBP
 * Ensures immutability and proper decimal handling for banking operations
 */
public final class Money {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    @NotNull
    @DecimalMin(value = "0.00", message = "Amount cannot be negative")
    private final BigDecimal amount;

    private final String currency;

    private Money(BigDecimal amount, String currency) {
        this.amount = amount.setScale(SCALE, ROUNDING_MODE);
        this.currency = currency;
    }

    public static Money pounds(BigDecimal amount) {
        return new Money(amount, "GBP");
    }

    public static Money pounds(double amount) {
        return new Money(BigDecimal.valueOf(amount), "GBP");
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO, "GBP");
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot perform operation on different currencies");
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Money money = (Money) obj;
        return Objects.equals(amount, money.amount) && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", currency, amount);
    }
}
