package br.com.ukbank.domain.valueobjects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * Value Object representing UK phone numbers
 * Ensures UK phone number format validation and normalization
 */
public final class UKPhoneNumber {

    @NotBlank
    @Pattern(regexp = "^\\+44[0-9]{10}$", message = "Invalid UK phone number format")
    private final String number;

    private UKPhoneNumber(String number) {
        this.number = normalizePhoneNumber(number);
    }

    public static UKPhoneNumber of(String number) {
        String normalized = normalizePhoneNumber(number);
        if (!isValid(normalized)) {
            throw new IllegalArgumentException("Invalid UK phone number format");
        }
        return new UKPhoneNumber(normalized);
    }

    private static String normalizePhoneNumber(String number) {
        if (number == null) return null;

        // Remove spaces and other characters
        String cleaned = number.replaceAll("[^0-9+]", "");

        // Convert 07xxx to +447xxx format
        if (cleaned.startsWith("07")) {
            cleaned = "+447" + cleaned.substring(2);
        } else if (cleaned.startsWith("447")) {
            cleaned = "+" + cleaned;
        }

        return cleaned;
    }

    private static boolean isValid(String number) {
        return number != null && number.matches("^\\+44[0-9]{10}$");
    }

    public String getNumber() {
        return number;
    }

    public String getDisplayFormat() {
        // Convert +447911123456 to +44 7911 123456
        return String.format("%s %s %s",
            number.substring(0, 3),
            number.substring(3, 7),
            number.substring(7));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UKPhoneNumber that = (UKPhoneNumber) obj;
        return Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return number;
    }
}
