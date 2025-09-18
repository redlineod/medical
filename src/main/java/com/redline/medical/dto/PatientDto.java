package com.redline.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientDto {
    private String firstName;
    private String lastName;
    private List<VisitDto> lastVisits;
}
