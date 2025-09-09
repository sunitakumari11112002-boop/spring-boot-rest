package br.com.ukbank.application.services;

import br.com.ukbank.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for publishing domain events
 * Coordinates event handling across the application
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DomainEventPublisher {

    // In a real application, this would integrate with Spring ApplicationEventPublisher
    // or a message broker like RabbitMQ/Kafka

    /**
     * Publishes domain events for async processing
     */
    public void publish(DomainEvent event) {
        log.info("Publishing domain event: {}", event.getClass().getSimpleName());
        // Implementation would publish to event bus/message broker
        log.debug("Event details: {}", event);
    }
}
