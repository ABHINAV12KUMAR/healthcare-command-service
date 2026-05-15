package com.healthcare.command.outbox.scheduler;
import com.healthcare.command.kafka.PatientProducer;
import com.healthcare.command.outbox.entity.OutboxEvent;
import com.healthcare.command.outbox.repository.OutboxRepository;
import com.healthcare.model.PatientEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final PatientProducer producer;
    private final ObjectMapper objectMapper;

    // Har 5 second me chalega
    @Scheduled(fixedRate = 5000)
    public void publishEvents() {
        log.info("Checking for unprocessed outbox events...");
        List<OutboxEvent> events = outboxRepository.findByProcessedFalse();
        for (OutboxEvent e : events) {
            try {
                // JSON → Object
                PatientEvent event =
                        objectMapper.readValue(e.getPayload(), PatientEvent.class);
                // Kafka me bhejo
                producer.sendEvent(event);
                // Mark processed
                e.setProcessed(true);
                outboxRepository.save(e);
                log.info("Event published successfully for id: {}", e.getId());
            } catch (Exception ex) {
                log.error("Failed to process event id: {}", e.getId(), ex);
            }
        }
    }
}