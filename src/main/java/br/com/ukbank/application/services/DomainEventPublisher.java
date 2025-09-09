package br.com.ukbank.application.services;

import br.com.ukbank.domain.events.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Domain Event Publisher following Event-Driven Architecture
 * Publishes domain events for audit trails and cross-cutting concerns
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Publish domain event to event bus
     */
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {}", event.getEventType());
        applicationEventPublisher.publishEvent(event);
        log.info("Domain event published: {} at {}", event.getEventType(), event.getOccurredOn());
    }
}
