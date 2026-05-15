package com.healthcare.command.mapper;

import com.healthcare.command.response.PatientResponse;
import com.healthcare.model.Patient;
import com.healthcare.model.PatientDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    Patient toEntity(PatientDTO dto);
    PatientResponse toResponse(Patient patient);
}