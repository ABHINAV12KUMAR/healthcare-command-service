package com.healthcare.command.outbox.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.command.outbox.entity.OutboxEvent;
import com.healthcare.command.outbox.repository.OutboxRepository;
import com.healthcare.model.PatientEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void saveEvent(PatientEvent event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = new OutboxEvent();
            outbox.setEventType(event.getEventType());
            outbox.setPayload(payload);
            outbox.setProcessed(false);
            outbox.setCreatedAt(LocalDateTime.now());

            outboxRepository.save(outbox);

            log.info("Outbox event saved for patient id: {}", event.getPatientId());

        } catch (Exception e) {
            log.error("Error saving outbox event", e);
            throw new RuntimeException("Outbox save failed");
        }
    }
}