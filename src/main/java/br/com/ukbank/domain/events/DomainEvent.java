package br.com.ukbank.domain.events;

import java.time.LocalDateTime;

/**
 * Base class for all domain events in the banking system
 * Follows Event Sourcing pattern for audit trails
 */
public abstract class DomainEvent {

    private final String eventId;
    private final LocalDateTime occurredOn;
    private final String eventType;

    protected DomainEvent(String eventType) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.eventType = eventType;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public String getEventType() {
        return eventType;
    }

    @Override
    public String toString() {
        return String.format("Event[id=%s, type=%s, occurredOn=%s]",
            eventId, eventType, occurredOn);
    }
}
