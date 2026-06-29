package com.healthcare.command.service;

import com.healthcare.command.exception.ResourceNotFoundException;
import com.healthcare.command.kafka.PatientProducer;
import com.healthcare.command.mapper.PatientMapper;
import com.healthcare.command.outbox.service.OutboxService;
import com.healthcare.command.repository.PatientRepository;

import com.healthcare.command.response.PatientResponse;
import com.healthcare.model.Patient;
import com.healthcare.model.PatientDTO;
import com.healthcare.model.PatientEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PatientProducer patientProducer;
    private final OutboxService outboxService;
    private final MeterRegistry meterRegistry;

    private final Counter patientCreatedCounter;
    private final Counter patientUpdatedCounter;
    private final Counter patientDeletedCounter;
    private final Timer patientCreateTimer;
    private final Timer patientUpdateTimer;
    private final Timer patientDeleteTimer;

    @Transactional
    public PatientResponse createPatient(PatientDTO dto) {
        return patientCreateTimer.record(() -> {
            log.info("Creating patient: {}", dto.getName());

            Patient patient = patientMapper.toEntity(dto);
            Patient saved = patientRepository.save(patient);

            log.info("Patient saved with id: {}", saved.getId());

            PatientEvent event = new PatientEvent();
            event.setEventType("CREATE");
            event.setPatientId(saved.getId());
            event.setName(saved.getName());
            event.setDisease(saved.getDisease());
            event.setEmail(saved.getEmail());

            log.info("Publishing Kafka event: {}", event);

            log.info("Saving event to Outbox...");
            outboxService.saveEvent(event);

            patientCreatedCounter.increment();
            return patientMapper.toResponse(saved);
        });
    }

    public PatientResponse updatePatient(Long Id, PatientDTO dto) {
        return patientUpdateTimer.record(() -> {
            Patient patient = patientRepository.findById(Id)
                    .orElseThrow(() -> new RuntimeException("Patient Not Found"));
            patient.setName(dto.getName());
            patient.setDisease(dto.getDisease());
            Patient saved = patientRepository.save(patient);
            // Add the Kafka Event Logic
            PatientEvent event = new PatientEvent();
            event.setEventType("UPDATE");
            event.setPatientId(saved.getId());
            event.setName(saved.getName());
            event.setDisease(saved.getDisease());
            patientProducer.sendEvent(event);
            patientUpdatedCounter.increment();
            return patientMapper.toResponse(saved);
        });
    }

    public void deletePatient(Long id) {
        patientDeleteTimer.record(() -> {
            Patient patient = patientRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            // Add the kafka Event Delete Logic
            PatientEvent event = new PatientEvent();
            event.setEventType("DELETE");
            event.setPatientId(id);
            patientProducer.sendEvent(event);
            patientRepository.delete(patient);
            patientDeletedCounter.increment();
        });
    }
}
