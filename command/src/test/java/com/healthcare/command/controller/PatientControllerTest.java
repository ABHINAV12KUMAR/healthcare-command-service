package com.healthcare.command.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthcare.command.response.ApiResponse;
import com.healthcare.command.response.PatientResponse;
import com.healthcare.command.service.PatientService;
import com.healthcare.model.PatientDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private PatientDTO patientDTO;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();

        patientDTO = new PatientDTO();
        patientDTO.setName("John Doe");
        patientDTO.setDisease("Flu");
        patientDTO.setEmail("john@example.com");

        patientResponse = new PatientResponse();
        patientResponse.setId(1L);
        patientResponse.setName("John Doe");
        patientResponse.setDisease("Flu");
        patientResponse.setEmail("john@example.com");
    }

    @Test
    void createPatient_Success() throws Exception {
        when(patientService.createPatient(any(PatientDTO.class))).thenReturn(patientResponse);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patientDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Patient created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.disease").value("Flu"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void updatePatient_Success() throws Exception {
        PatientDTO updateDTO = new PatientDTO();
        updateDTO.setName("Jane Doe");
        updateDTO.setDisease("Cold");

        PatientResponse updatedResponse = new PatientResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Jane Doe");
        updatedResponse.setDisease("Cold");
        updatedResponse.setEmail("john@example.com");

        when(patientService.updatePatient(eq(1L), any(PatientDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/patients/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Patient updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Jane Doe"))
                .andExpect(jsonPath("$.data.disease").value("Cold"));
    }

    @Test
    void deletePatient_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Patient deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
