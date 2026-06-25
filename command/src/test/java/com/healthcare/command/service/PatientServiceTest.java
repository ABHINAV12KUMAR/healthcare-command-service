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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PatientProducer patientProducer;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private PatientService patientService;

    private PatientDTO patientDTO;
    private Patient patient;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        patientDTO = new PatientDTO();
        patientDTO.setName("John Doe");
        patientDTO.setDisease("Flu");
        patientDTO.setEmail("john@example.com");

        patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");
        patient.setDisease("Flu");
        patient.setEmail("john@example.com");

        patientResponse = new PatientResponse();
        patientResponse.setId(1L);
        patientResponse.setName("John Doe");
        patientResponse.setDisease("Flu");
        patientResponse.setEmail("john@example.com");
    }

    @Test
    void createPatient_Success() {
        when(patientMapper.toEntity(patientDTO)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toResponse(patient)).thenReturn(patientResponse);

        PatientResponse result = patientService.createPatient(patientDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("Flu", result.getDisease());
        assertEquals("john@example.com", result.getEmail());

        verify(patientMapper, times(1)).toEntity(patientDTO);
        verify(patientRepository, times(1)).save(patient);
        verify(outboxService, times(1)).saveEvent(any(PatientEvent.class));
        verify(patientMapper, times(1)).toResponse(patient);
    }

    @Test
    void updatePatient_Success() {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setName("Jane Doe");
        updateDTO.setDisease("Cold");

        Patient updatedPatient = new Patient();
        updatedPatient.setId(1L);
        updatedPatient.setName("Jane Doe");
        updatedPatient.setDisease("Cold");
        updatedPatient.setEmail("john@example.com");

        PatientResponse updatedResponse = new PatientResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Jane Doe");
        updatedResponse.setDisease("Cold");
        updatedResponse.setEmail("john@example.com");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);
        when(patientMapper.toResponse(updatedPatient)).thenReturn(updatedResponse);

        PatientResponse result = patientService.updatePatient(1L, updateDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("Cold", result.getDisease());

        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(patientProducer, times(1)).sendEvent(any(PatientEvent.class));
        verify(patientMapper, times(1)).toResponse(updatedPatient);
    }

    @Test
    void updatePatient_PatientNotFound() {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setName("Jane Doe");

        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            patientService.updatePatient(1L, updateDTO);
        });

        assertEquals("Patient Not Found", exception.getMessage());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
        verify(patientProducer, never()).sendEvent(any(PatientEvent.class));
    }

    @Test
    void deletePatient_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        doNothing().when(patientRepository).delete(patient);

        patientService.deletePatient(1L);

        verify(patientRepository, times(1)).findById(1L);
        verify(patientProducer, times(1)).sendEvent(any(PatientEvent.class));
        verify(patientRepository, times(1)).delete(patient);
    }

    @Test
    void deletePatient_PatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            patientService.deletePatient(1L);
        });

        assertEquals("Patient not found", exception.getMessage());
        verify(patientRepository, times(1)).findById(1L);
        verify(patientProducer, never()).sendEvent(any(PatientEvent.class));
        verify(patientRepository, never()).delete(any(Patient.class));
    }
}
