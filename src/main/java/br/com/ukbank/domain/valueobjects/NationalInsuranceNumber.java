package br.com.ukbank.domain.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Value object representing UK National Insurance Number
 * Immutable and self-validating
 */
@Embeddable
@Getter
@EqualsAndHashCode
public class NationalInsuranceNumber {

    private static final Pattern NI_PATTERN = Pattern.compile("^[A-Z]{2}[0-9]{6}[A-Z]$");

    private String value;

    // Default constructor for JPA
    protected NationalInsuranceNumber() {}

    private NationalInsuranceNumber(String value) {
        this.value = validateNationalInsuranceNumber(value);
    }

    public static NationalInsuranceNumber of(String value) {
        return new NationalInsuranceNumber(value);
    }

    private String validateNationalInsuranceNumber(String value) {
        if (value == null || !NI_PATTERN.matcher(value.toUpperCase()).matches()) {
            throw new IllegalArgumentException("Invalid UK National Insurance Number format");
        }
        return value.toUpperCase();
    }

    @Override
    public String toString() {
        return value;
    }
}
