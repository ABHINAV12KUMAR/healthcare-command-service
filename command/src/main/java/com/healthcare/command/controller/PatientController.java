package com.healthcare.command.controller;

import com.healthcare.command.response.ApiResponse;
import com.healthcare.command.response.PatientResponse;
import com.healthcare.command.service.PatientService;
import com.healthcare.model.PatientDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody PatientDTO patientDTO) {

        PatientResponse savedPatient = patientService.createPatient(patientDTO);

        ApiResponse<PatientResponse> response = new ApiResponse<>(
                "Patient created successfully",
                savedPatient
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO dto
    ) {
        PatientResponse update = patientService.updatePatient(id, dto);

        ApiResponse<PatientResponse> response = new ApiResponse<>("Patient updated successfully", update);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {

        patientService.deletePatient(id);

        ApiResponse<Void> response =
                new ApiResponse<>("Patient deleted successfully", null);

        return ResponseEntity.ok(response);
    }
}