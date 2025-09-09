package br.com.ukbank.domain.events;

/**
 * Domain event fired when a customer is registered
 * Used for audit trails and event sourcing
 */
public class CustomerRegisteredEvent extends DomainEvent {

    private final Long customerId;
    private final String firstName;
    private final String lastName;
    private final String email;

    public CustomerRegisteredEvent(Long customerId, String firstName, String lastName, String email) {
        super("CUSTOMER_REGISTERED");
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
