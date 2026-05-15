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

    @Transactional
    public PatientResponse createPatient(PatientDTO dto) {
        log.info("Creating patient: {}", dto.getName());
        Patient patient = patientMapper.toEntity(dto);
        Patient saved = patientRepository.save(patient);//5 ms
        log.info("Patient saved with id: {}", saved.getId());
        // Add kafka Event Logic create
        PatientEvent event =new PatientEvent();
        event.setEventType("CREATE");
        event.setPatientId(saved.getId());
        log.info("Publishing Kafka event: {}", event);
        event.setName(saved.getName());
        event.setDisease(saved.getDisease());

        log.info("Saving event to Outbox...");
        outboxService.saveEvent(event);
        //patientProducer.sendEvent(event);
        return patientMapper.toResponse(saved);
    }

    public PatientResponse updatePatient(Long Id, PatientDTO dto){
        Patient patient = patientRepository.findById(Id)
                .orElseThrow(()->new RuntimeException("Patient Not Found"));
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
        return patientMapper.toResponse(saved);
    }

    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        // Add the kafka Event Delete Logic
        PatientEvent event = new PatientEvent();
        event.setEventType("DELETE");
        event.setPatientId(id);
        patientProducer.sendEvent(event);
        patientRepository.delete(patient);
    }
}
