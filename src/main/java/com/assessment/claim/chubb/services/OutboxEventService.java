package com.assessment.claim.chubb.services;

import com.assessment.claim.chubb.entities.OutboxEvent;
import com.assessment.claim.chubb.repository.OutboxEventRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
/**
 * Serializes domain events and persists them with the current database
 * transaction.
 */
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    /** Adds a pending event to the outbox without publishing it directly. */
    public void enqueue(String destination, Object event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType(event.getClass().getName());
        outboxEvent.setDestination(destination);
        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));
        } catch (JacksonException exception) {
            throw new IllegalStateException("Could not serialize outbox event", exception);
        }
        outboxEventRepository.save(outboxEvent);
    }
}