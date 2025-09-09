package br.com.ukbank.domain.valueobjects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * Value Object representing UK National Insurance Number
 * Ensures format validation and immutability
 */
public final class NationalInsuranceNumber {

    @NotBlank
    @Pattern(regexp = "^[A-Z]{2}[0-9]{6}[A-Z]$", message = "Invalid UK National Insurance Number format")
    private final String value;

    private NationalInsuranceNumber(String value) {
        this.value = value.toUpperCase();
    }

    public static NationalInsuranceNumber of(String value) {
        if (value == null || !isValid(value)) {
            throw new IllegalArgumentException("Invalid National Insurance Number format");
        }
        return new NationalInsuranceNumber(value);
    }

    private static boolean isValid(String value) {
        return value.matches("^[A-Za-z]{2}[0-9]{6}[A-Za-z]$");
    }

    public String getValue() {
        return value;
    }

    public String getFormattedValue() {
        return String.format("%s %s %s %s",
            value.substring(0, 2),
            value.substring(2, 4),
            value.substring(4, 6),
            value.substring(6, 9));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NationalInsuranceNumber that = (NationalInsuranceNumber) obj;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
