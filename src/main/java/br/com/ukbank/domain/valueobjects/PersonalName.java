package br.com.ukbank.domain.valueobjects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Value object representing a person's full name
 * Immutable and self-validating
 */
@Embeddable
@Getter
@EqualsAndHashCode
public class PersonalName {

    private String firstName;
    private String lastName;

    // Default constructor for JPA
    protected PersonalName() {}

    private PersonalName(String firstName, String lastName) {
        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");
    }

    public static PersonalName of(String firstName, String lastName) {
        return new PersonalName(firstName, lastName);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    private String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException(fieldName + " cannot exceed 50 characters");
        }
        return name.trim();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
