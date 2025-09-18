package com.redline.medical.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitCreateRequest {

    @NotNull(message = "Start time is required")
    private LocalDateTime start;

    @NotNull(message = "End time is required")
    private LocalDateTime end;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;
}
