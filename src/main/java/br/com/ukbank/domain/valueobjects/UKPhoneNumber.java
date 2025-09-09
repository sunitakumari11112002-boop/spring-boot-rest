package br.com.ukbank.domain.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Value object representing UK phone numbers
 * Immutable and self-validating
 */
@Embeddable
@Getter
@EqualsAndHashCode
public class UKPhoneNumber {

    private static final Pattern UK_PHONE_PATTERN = Pattern.compile("^\\+44[0-9]{10}$");

    private String number;

    // Default constructor for JPA
    protected UKPhoneNumber() {}

    private UKPhoneNumber(String number) {
        this.number = validatePhoneNumber(number);
    }

    public static UKPhoneNumber of(String number) {
        return new UKPhoneNumber(number);
    }

    private String validatePhoneNumber(String number) {
        if (number == null || !UK_PHONE_PATTERN.matcher(number).matches()) {
            throw new IllegalArgumentException("Invalid UK phone number format. Expected: +44xxxxxxxxxx");
        }
        return number;
    }

    @Override
    public String toString() {
        return number;
    }
}
