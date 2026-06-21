package com.healthcare.command.response;
import lombok.Data;

@Data
public class PatientResponse {

    private Long id;
    private String name;
    private String disease;
    private String email;
}