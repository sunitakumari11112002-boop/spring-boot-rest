package br.com.ukbank.domain.valueobjects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Value Object for personal names
 * Made public for access from application layer
 */
@Embeddable
public class PersonalName {
    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    protected PersonalName() {}

    private PersonalName(String firstName, String lastName) {
        this.firstName = validateName(firstName, "First name");
        this.lastName = validateName(lastName, "Last name");
    }

    public static PersonalName of(String firstName, String lastName) {
        return new PersonalName(firstName, lastName);
    }

    private String validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty() || name.length() > 50) {
            throw new IllegalArgumentException(fieldName + " is required and must not exceed 50 characters");
        }
        return name.trim();
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
}
