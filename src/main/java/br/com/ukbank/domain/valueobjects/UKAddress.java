package br.com.ukbank.domain.valueobjects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * Value Object representing UK postal addresses and postcodes
 * Ensures UK address format validation and immutability
 */
public final class UKAddress {

    @NotBlank
    private final String addressLine;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$",
             message = "Invalid UK postcode format")
    private final String postcode;

    private UKAddress(String addressLine, String postcode) {
        this.addressLine = addressLine;
        this.postcode = postcode.toUpperCase();
    }

    public static UKAddress of(String addressLine, String postcode) {
        if (addressLine == null || addressLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Address line cannot be empty");
        }
        if (!isValidPostcode(postcode)) {
            throw new IllegalArgumentException("Invalid UK postcode format");
        }
        return new UKAddress(addressLine.trim(), postcode);
    }

    private static boolean isValidPostcode(String postcode) {
        return postcode != null &&
               postcode.toUpperCase().matches("^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][A-Z]{2}$");
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getFullAddress() {
        return String.format("%s, %s", addressLine, postcode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UKAddress ukAddress = (UKAddress) obj;
        return Objects.equals(addressLine, ukAddress.addressLine) &&
               Objects.equals(postcode, ukAddress.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressLine, postcode);
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
}
