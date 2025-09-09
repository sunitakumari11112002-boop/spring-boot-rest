package br.com.ukbank.domain.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

/**
 * Value object representing UK addresses
 * Immutable and self-validating
 */
@Embeddable
@Getter
@EqualsAndHashCode
public class UKAddress {

    private static final Pattern UK_POSTCODE_PATTERN = Pattern.compile("^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$");

    private String addressLine;
    private String postcode;

    // Default constructor for JPA
    protected UKAddress() {}

    private UKAddress(String addressLine, String postcode) {
        this.addressLine = validateAddressLine(addressLine);
        this.postcode = validatePostcode(postcode);
    }

    public static UKAddress of(String addressLine, String postcode) {
        return new UKAddress(addressLine, postcode);
    }

    private String validateAddressLine(String addressLine) {
        if (addressLine == null || addressLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Address line cannot be null or empty");
        }
        if (addressLine.length() > 200) {
            throw new IllegalArgumentException("Address line cannot exceed 200 characters");
        }
        return addressLine.trim();
    }

    private String validatePostcode(String postcode) {
        if (postcode == null || !UK_POSTCODE_PATTERN.matcher(postcode.toUpperCase()).matches()) {
            throw new IllegalArgumentException("Invalid UK postcode format");
        }
        return postcode.toUpperCase();
    }

    @Override
    public String toString() {
        return addressLine + ", " + postcode;
    }
}
